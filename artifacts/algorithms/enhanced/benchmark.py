# benchmark.py
# Empirical comparison of the original vs. enhanced data access patterns.
#
# Run this after the database is populated to produce evidence of the
# complexity improvements described in the narrative.

import time
from CRUD_Python_Module import AnimalShelter
from rescue_config import build_query


def time_it(label, func, iterations=20):
    """Run func N times and return the average duration in milliseconds."""
    start = time.perf_counter()
    for _ in range(iterations):
        func()
    elapsed = (time.perf_counter() - start) / iterations * 1000
    print(f"{label:55s} {elapsed:8.3f} ms")
    return elapsed


def main():
    username = input("MongoDB username: ")
    password = input("MongoDB password: ")

    shelter = AnimalShelter(username, password)
    query = build_query('water')

    print("\n--- Warm-up (populates indexes and cache) ---")
    shelter.read(query, page=1, page_size=100)
    shelter.aggregate_breed_counts(query)

    print("\n--- Pagination: memory is bounded by page_size ---")
    time_it("read() page=1,  page_size=100",
            lambda: shelter.read(query, page=1, page_size=100))
    time_it("read() page=1,  page_size=1000",
            lambda: shelter.read(query, page=1, page_size=1000))

    print("\n--- Cache effect: second identical call is O(1) ---")
    shelter.clear_cache()
    time_it("read() with cold cache", lambda: shelter.read(query, page=1, page_size=100))
    time_it("read() with warm cache", lambda: shelter.read(query, page=1, page_size=100))

    print("\n--- Server-side aggregation vs. full document transfer ---")
    time_it("aggregate_breed_counts()",
            lambda: shelter.aggregate_breed_counts(query))

    print("\n--- Count only (no documents transferred) ---")
    time_it("count()", lambda: shelter.count(query))

    shelter.close()


if __name__ == '__main__':
    main()