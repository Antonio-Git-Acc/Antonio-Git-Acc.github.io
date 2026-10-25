# transform_data.py
# Converts the raw Austin Animal Center dataset (imported from CSV with
# original column names) into the snake_case schema expected by the
# enhanced CS 340 dashboard code.
#
# Run once after importing the raw CSV:
#   python transform_data.py

import re
import random
from pymongo import MongoClient, ASCENDING
from urllib.parse import quote_plus

# ----------------------------------------------------------------------
# Configuration
# ----------------------------------------------------------------------
DB_NAME = 'aac'
COLLECTION = 'animals'

# Austin Animal Center approximate coordinates (used as the center point
# for distributing synthetic location data, since the raw CSV lacks it).
AUSTIN_LAT = 30.2669
AUSTIN_LON = -97.6937
LOCATION_JITTER = 0.03  # roughly +/- 3 km


# ----------------------------------------------------------------------
# Age parsing
# ----------------------------------------------------------------------
def parse_age_to_weeks(age_str):
    """Convert an age string like '1 year 2 months' to an integer of weeks.

    Returns None if the input is empty or unrecognizable. Uses the same
    approximations the CS 340 dataset uses: 1 year = 52 weeks,
    1 month = 4 weeks.
    """
    if not age_str:
        return None
    text = str(age_str).strip().lower()
    if text in ('', 'unknown', 'none', 'null'):
        return None

    total = 0
    year_match = re.search(r'(\d+)\s*year', text)
    if year_match:
        total += int(year_match.group(1)) * 52
    month_match = re.search(r'(\d+)\s*month', text)
    if month_match:
        total += int(month_match.group(1)) * 4
    week_match = re.search(r'(\d+)\s*week', text)
    if week_match:
        total += int(week_match.group(1))

    return total if total > 0 else None


# ----------------------------------------------------------------------
# Document transformation
# ----------------------------------------------------------------------
def transform_document(doc):
    """Rename fields, compute age in weeks, and add location data."""
    age_text = doc.get('Age upon Outcome') or doc.get('age_upon_outcome')
    weeks = parse_age_to_weeks(age_text)

    return {
        'animal_id': doc.get('Animal ID') or doc.get('animal_id'),
        'name': doc.get('Name', '') or doc.get('name', ''),
        'datetime': doc.get('DateTime') or doc.get('datetime'),
        'timestamp': doc.get('MonthYear') or doc.get('timestamp'),
        'outcome_type': doc.get('Outcome Type') or doc.get('outcome_type'),
        'outcome_subtype': doc.get('Outcome Subtype') or doc.get('outcome_subtype'),
        'animal_type': doc.get('Animal Type') or doc.get('animal_type'),
        'sex_upon_outcome': doc.get('Sex upon Outcome') or doc.get('sex_upon_outcome'),
        'age_upon_outcome': age_text,
        'age_upon_outcome_in_weeks': weeks,
        'breed': doc.get('Breed') or doc.get('breed'),
        'color': doc.get('Color') or doc.get('color'),
        'monthyear': doc.get('MonthYear') or doc.get('monthyear'),
        'date_of_birth': doc.get('Date of Birth') or doc.get('date_of_birth'),
        # Synthetic location - see note in script header
        'location_lat': AUSTIN_LAT + random.uniform(-LOCATION_JITTER, LOCATION_JITTER),
        'location_long': AUSTIN_LON + random.uniform(-LOCATION_JITTER, LOCATION_JITTER),
    }


# ----------------------------------------------------------------------
# Main
# ----------------------------------------------------------------------
def main():
    username = input("MongoDB username: ")
    password = input("MongoDB password: ")

    connection_string = (
        f'mongodb://{quote_plus(username)}:{quote_plus(password)}'
        f'@localhost:27017/?authSource=admin'
    )
    client = MongoClient(connection_string)
    db = client[DB_NAME]
    raw = db[COLLECTION]
    staged = db['animals_staged']

    total = raw.count_documents({})
    if total == 0:
        print("The 'animals' collection is empty. Import the CSV first.")
        return

    print(f"Found {total} documents to transform.")

    # Clear any previous staging collection
    staged.drop()

    # Transform in batches so memory stays bounded
    batch = []
    processed = 0
    for doc in raw.find():
        batch.append(transform_document(doc))
        if len(batch) >= 1000:
            staged.insert_many(batch)
            processed += len(batch)
            batch = []
            print(f"  Processed {processed}/{total}")
    if batch:
        staged.insert_many(batch)
        processed += len(batch)

    print(f"Staged {processed} transformed documents.")

    # Create indexes on the staged collection so queries are fast
    print("Creating indexes...")
    staged.create_index(
        [('breed', ASCENDING),
         ('sex_upon_outcome', ASCENDING),
         ('age_upon_outcome_in_weeks', ASCENDING)],
        name='rescue_query_idx'
    )
    staged.create_index([('animal_id', ASCENDING)], name='animal_id_idx')

    # Replace the original collection with the transformed data
    print("Replacing original collection with transformed data...")
    raw.drop()
    staged.rename(COLLECTION)

    print("Done. The 'animals' collection now uses the enhanced schema.")
    print(f"Verify with: db.{COLLECTION}.findOne()")
    client.close()


if __name__ == '__main__':
    main()