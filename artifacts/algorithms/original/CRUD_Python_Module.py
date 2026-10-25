# CRUD_Python_Module.py
# Full CRUD implementation for Austin Animal Center data

from pymongo import MongoClient
from bson.objectid import ObjectId

class AnimalShelter(object):
    """ CRUD operations for Animal collection in MongoDB """

    def __init__(self, username, password, host='localhost', port=27017, auth_db='admin'):
        """
        Initialize connection to MongoDB.
        :param username: MongoDB username
        :param password: MongoDB password
        :param host: MongoDB host (default localhost)
        :param port: MongoDB port (default 27017)
        :param auth_db: Authentication database (default admin)
        """
        DB = 'aac'
        COL = 'animals'
        # Build connection string with provided credentials
        connection_string = f'mongodb://{username}:{password}@{host}:{port}/?authSource={auth_db}'
        self.client = MongoClient(connection_string)
        self.database = self.client[DB]
        self.collection = self.database[COL]

    def create(self, data):
        """Insert a document into the collection.
        :param data: dictionary to insert
        :return: True if successful, False otherwise
        """
        if data is None:
            return False
        try:
            result = self.collection.insert_one(data)
            return result.acknowledged and result.inserted_id is not None
        except Exception as e:
            print(f"Insert failed: {e}")
            return False

    def read(self, query):
        """Find documents matching the query.
        :param query: dictionary with search criteria
        :return: list of matching documents (empty list if none or error)
        """
        if query is None:
            return []
        try:
            cursor = self.collection.find(query)
            return list(cursor)
        except Exception as e:
            print(f"Query failed: {e}")
            return []

    def update(self, query, new_values):
        """Update one or more documents matching the query.
        :param query: dictionary specifying which documents to update
        :param new_values: dictionary of update operators (e.g., {"$set": {...}})
        :return: number of documents modified (int)
        """
        if query is None or new_values is None:
            return 0
        try:
            result = self.collection.update_many(query, new_values)
            return result.modified_count
        except Exception as e:
            print(f"Update failed: {e}")
            return 0

    def delete(self, query):
        """Delete all documents matching the query.
        :param query: dictionary specifying which documents to delete
        :return: number of documents deleted (int)
        """
        if query is None:
            return 0
        try:
            result = self.collection.delete_many(query)
            return result.deleted_count
        except Exception as e:
            print(f"Delete failed: {e}")
            return 0