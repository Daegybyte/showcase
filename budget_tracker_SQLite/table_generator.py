import sqlite3
class Table_Generator:
    MY_DB = "./src/data.sqlite"
    TABLES = ["Categories", "Income", "Expenses","Budget"]
    
    def __init__(self):
        pass


    def create_dummy_categories(self) -> None:
        conn = sqlite3.connect(self.MY_DB)
        c = conn.cursor()
        
        c.execute('''CREATE TABLE Categories
                  (category_id INTEGER PRIMARY KEY,
                  category_name TEXT)''')
        
       # Define the initial categories as a list of tuples
        categories = [
            ('Food',),
            ('Transportation',),
            ('Entertainment',),
            ('Housing',),
            ('Utilities',),
            ('Clothing',),
            ('Healthcare',),
            ('Education',),
            ('Travel',),
            ('Miscellaneous',),
        ]
        
        
        insert_categories = "INSERT INTO Categories (category_name) VALUES (?)"
        c.executemany(insert_categories, categories)
        
        self.__commit_and_close(conn)
  
    def create_dummy_income(self) -> None:
        conn = sqlite3.connect(self.MY_DB)
        c = conn.cursor()
        
        c.execute('''CREATE TABLE Income
            (income_id INTEGER PRIMARY KEY,
            date TEXT,
            amount REAL,
            source TEXT)''')
        
        c.execute("INSERT INTO Income (date, amount, source) VALUES ('2023-03-24', 110.25,'Work')")
        c.execute("INSERT INTO Income (date, amount, source) VALUES ('2023-03-31', 210.25,'Side Hustle')")
        c.execute("INSERT INTO Income (date, amount, source) VALUES ('2023-03-31', 50.00,'Grandma')")
        
        self.__commit_and_close(conn)
      
              
    def create_dummy_expenses(self) -> None:
        conn = sqlite3.connect(Table_Generator.MY_DB)
        c = conn.cursor()
        # Create the Expenses table
        c.execute('''CREATE TABLE Expenses
                    (expense_id INTEGER PRIMARY KEY,
                    date TEXT,
                    amount REAL,
                    category_id INTEGER,
                    notes TEXT,
                    FOREIGN KEY (category_id) REFERENCES Categories(category_id))''')
        
        # Insert some dummy expense data
        c.execute("INSERT INTO Expenses (date, amount, category_id, notes) VALUES ('2023-04-24', 12.50, 1, 'Lunch with friends')")
        c.execute("INSERT INTO Expenses (date, amount, category_id, notes) VALUES ('2023-04-25', 32.75, 2, 'Gas for car')")
            
        # Commit the changes and close the connection
        self.__commit_and_close(conn)
        
  
        
    def create_dummy_budget(self) -> None:
        conn = sqlite3.connect(self.MY_DB)
        c = conn.cursor()
        
        c.execute('''CREATE TABLE Budget
             (budget_id INTEGER PRIMARY KEY,
              category_id INTEGER,
              budget_amount REAL,
              actual_amount REAL,
              FOREIGN KEY (category_id) REFERENCES Categories(category_id))''')
        
        self.__commit_and_close(conn)
        

        
    def __commit_and_close(self, conn) -> None:
        conn.commit()
        conn.close()
        
    
    def CAUTION_drop_all_tables(self, table_names):
        conn = sqlite3.connect(self.MY_DB)
        c = conn.cursor()

        # Drop each table in the table_names list if it exists
        for table_name in table_names:
            c.execute(f'DROP TABLE IF EXISTS {table_name}')

        # Commit the changes and close the connection
        self.__commit_and_close(conn)
        
    if __name__ == "__main__":
        pass