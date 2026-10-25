# rescue_config.py
# Centralized configuration for Grazioso Salvare rescue-type filtering.
#
# Enhancement rationale: the original build_query() function hardcoded the
# breed lists and age ranges inline. Extracting them here eliminates magic
# values, removes duplication, and makes new rescue categories data-driven.

RESCUE_TYPES = {
    'water': {
        'label': 'Water Rescue',
        'breeds': ['Labrador Retriever Mix',
                   'Chesapeake Bay Retriever',
                   'Newfoundland'],
        'sex': 'Intact Female',
        'age_min': 26,
        'age_max': 156,
    },
    'mountain': {
        'label': 'Mountain / Wilderness Rescue',
        'breeds': ['German Shepherd',
                   'Alaskan Malamute',
                   'Old English Sheepdog',
                   'Siberian Husky',
                   'Rottweiler'],
        'sex': 'Intact Male',
        'age_min': 26,
        'age_max': 156,
    },
    'disaster': {
        'label': 'Disaster / Individual Tracking',
        'breeds': ['Doberman Pinscher',
                   'German Shepherd',
                   'Golden Retriever',
                   'Bloodhound',
                   'Rottweiler'],
        'sex': 'Intact Male',
        'age_min': 20,
        'age_max': 300,
    },
    'reset': {
        'label': 'Reset (Show All)',
        'breeds': None,
        'sex': None,
        'age_min': None,
        'age_max': None,
    },
}

# Maximum number of rows the dashboard will load into memory for display.
# This bounds the O(k) memory footprint of the read() call. Full server-side
# pagination for the DataTable is documented as future work.
MAX_DISPLAY_ROWS = 2000


def build_query(filter_type):
    """Build a MongoDB query dict from the centralized config.

    Raises ValueError for an unknown filter type rather than silently
    defaulting to 'reset', which was a finding in the code review.
    """
    if filter_type not in RESCUE_TYPES:
        raise ValueError(f"Unknown filter type: {filter_type}")

    cfg = RESCUE_TYPES[filter_type]
    if cfg['breeds'] is None:
        return {}  # 'reset' -> no filter

    return {
        'breed': {'$in': cfg['breeds']},
        'sex_upon_outcome': cfg['sex'],
        'age_upon_outcome_in_weeks': {
            '$gte': cfg['age_min'],
            '$lte': cfg['age_max'],
        },
    }


def get_radio_options():
    """Return Dash radio-button options built from the config."""
    return [{'label': ' ' + cfg['label'], 'value': key}
            for key, cfg in RESCUE_TYPES.items()]