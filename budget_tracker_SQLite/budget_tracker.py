import table_generator
import sqlite3

if __name__ == "__main__":
    T = table_generator.Table_Generator()
    try:
        T.create_dummy_categories()
    except sqlite3.OperationalError:
        print("Table 'Categories' already exists")
    try:
        T.create_dummy_income()
    except sqlite3.OperationalError:
        print("Table 'Income' already exists")
    try:
        T.create_dummy_expenses()
    except sqlite3.OperationalError:
        print("Table 'Expenses' already exists")
    try:
        T.create_dummy_budget()
    except sqlite3.OperationalError:
        print("Table 'Budgets' already exists")
    
    # try:
    #     T.CAUTION_drop_all_tables(T.TABLES)
    #     print("Bobby Tables was here")
    # except sqlite3.OperationalError:
    #     print("No tables to drop")
        
        