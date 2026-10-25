# CRUD_Python_Module.py
# Enhanced CRUD implementation for Austin Animal Center data.
# CS 499 Milestone Three - Algorithms and Data Structures enhancement.
#
# Enhancements over the original:
#   1. Compound + geospatial indexes  -> O(n) scans become O(log n) lookups
#   2. Paginated read()               -> O(n) memory becomes O(k) memory
#   3. Server-side aggregation        -> counting moves from Python to MongoDB
#   4. TTL query cache                -> repeated queries become O(1)
#   5. Input validation               -> all public methods guard their inputs
#   6. Explicit close()               -> connection is released deterministically
#   7. URL-encoded credentials        -> passwords with special chars no longer
#                                        break the connection string

from pymongo import MongoClient, ASCENDING, DESCENDING
from urllib.parse import quote_plus
import json
import time


class AnimalShelter(object):
    """CRUD operations for the Animal collection in MongoDB."""

    # Shared cache: {(query_key, page, page_size): (timestamp, results)}
    _query_cache = {}
    CACHE_TTL_SECONDS = 300  # 5 minutes

    def __init__(self, username, password, host='localhost', port=27017,
                 auth_db='admin'):
        DB = 'aac'
        COL = 'animals'

        # quote_plus protects passwords that contain characters with special
        # meaning in a MongoDB URI (for example '&' or '@').
        connection_string = (
            f'mongodb://{quote_plus(username)}:{quote_plus(password)}'
            f'@{host}:{port}/?authSource={auth_db}'
        )
        self.client = MongoClient(connection_string)
        self.database = self.client[DB]
        self.collection = self.database[COL]

        self._create_indexes()

    # ------------------------------------------------------------------
    # Index management
    # ------------------------------------------------------------------
    def _create_indexes(self):
        """Create indexes that reduce query time from O(n) to O(log n).

        The compound index matches the field order used by the rescue
        queries (breed -> sex -> age), so MongoDB can satisfy those queries
        with a single index scan. The 2dsphere index supports geospatial
        lookups on the location field. Index creation is idempotent.
        """
        self.collection.create_index(
            [('breed', ASCENDING),
             ('sex_upon_outcome', ASCENDING),
             ('age_upon_outcome_in_weeks', ASCENDING)],
            name='rescue_query_idx'
        )
        self.collection.create_index(
            [('location', '2dsphere')],
            name='location_geo_idx'
        )
        self.collection.create_index(
            [('name', ASCENDING)],
            name='name_idx'
        )

    # ------------------------------------------------------------------
    # Cache helpers
    # ------------------------------------------------------------------
    def _cache_key(self, query, page, page_size):
        """Build a hashable key from an unhashable MongoDB query dict.

        Uses json.dumps with sort_keys=True so nested dicts (like
        {'$in': [...]} or {'$gte': x, '$lte': y}) serialize to a stable
        string that can be used as a dictionary key.
        """
        return (json.dumps(query, sort_keys=True, default=str), page, page_size)

    def _get_cached(self, key):
        entry = self._query_cache.get(key)
        if entry is None:
            return None
        timestamp, results = entry
        if time.time() - timestamp > self.CACHE_TTL_SECONDS:
            del self._query_cache[key]
            return None
        return results

    def _set_cached(self, key, results):
        self._query_cache[key] = (time.time(), results)

    def clear_cache(self):
        """Invalidate the cache. Called by every write operation."""
        self._query_cache.clear()

    # ------------------------------------------------------------------
    # CRUD operations
    # ------------------------------------------------------------------
    def create(self, data):
        """Insert a document. Returns True if successful, False otherwise."""
        if not isinstance(data, dict) or not data:
            return False
        try:
            result = self.collection.insert_one(data)
            self.clear_cache()
            return result.acknowledged and result.inserted_id is not None
        except Exception as e:
            print(f"Insert failed: {e}")
            return False

    def read(self, query, page=1, page_size=100, projection=None, sort=None):
        """Read documents with pagination and caching.

        Complexity: O(k) where k = page_size, instead of O(n). Repeated
        identical queries resolve in O(1) from the TTL cache.

        :param query: MongoDB filter dict
        :param page: 1-based page number
        :param page_size: number of documents per page
        :param projection: optional field projection
        :param sort: optional list of (field, direction) tuples
        :return: list of matching documents
        """
        if query is None:
            return []
        if page < 1 or page_size < 1:
            return []

        key = self._cache_key(query, page, page_size)
        cached = self._get_cached(key)
        if cached is not None:
            return cached

        try:
            skip = (page - 1) * page_size
            cursor = self.collection.find(query, projection)
            if sort:
                cursor = cursor.sort(sort)
            cursor = cursor.skip(skip).limit(page_size)
            results = list(cursor)
            self._set_cached(key, results)
            return results
        except Exception as e:
            print(f"Query failed: {e}")
            return []

    def count(self, query):
        """Count matching documents server-side.

        No documents are transferred; only the integer result is returned.
        """
        if query is None:
            return 0
        try:
            return self.collection.count_documents(query)
        except Exception as e:
            print(f"Count failed: {e}")
            return 0

    def aggregate_breed_counts(self, query):
        """Return breed frequency counts using MongoDB's aggregation pipeline.

        Moves the O(n) counting operation from Python to the database
        server. Only the aggregated results cross the network, so the
        transferred payload is proportional to the number of distinct
        breeds rather than the number of documents.
        """
        if query is None:
            return []
        pipeline = [
            {'$match': query},
            {'$group': {'_id': '$breed', 'count': {'$sum': 1}}},
            {'$sort': {'count': DESCENDING}},
        ]
        try:
            return list(self.collection.aggregate(pipeline))
        except Exception as e:
            print(f"Aggregation failed: {e}")
            return []

    def update(self, query, new_values):
        """Update documents matching query. Returns modified count."""
        if not isinstance(query, dict) or not isinstance(new_values, dict):
            return 0
        if not query or not new_values:
            return 0
        try:
            result = self.collection.update_many(query, new_values)
            self.clear_cache()
            return result.modified_count
        except Exception as e:
            print(f"Update failed: {e}")
            return 0

    def delete(self, query):
        """Delete documents matching query. Returns deleted count."""
        if not isinstance(query, dict) or not query:
            return 0
        try:
            result = self.collection.delete_many(query)
            self.clear_cache()
            return result.deleted_count
        except Exception as e:
            print(f"Delete failed: {e}")
            return 0

    # ------------------------------------------------------------------
    # Cleanup
    # ------------------------------------------------------------------
    def close(self):
        """Close the MongoDB connection and clear the cache."""
        self.clear_cache()
        self.client.close()