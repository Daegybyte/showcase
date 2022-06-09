import datetime as dt
import json

# Load in wordle word list
word_list = json.loads(open("word_list.json").read())


# returns the difference in days between two dates
# order of dates is irrelevant
def num_of_days(date1, date2):
    return abs((date2 - date1).days)


# get the dates
start_date = dt.date(2021, 6, 19)
today = dt.date.today()

# index_of_word is the difference in the two dates
# when reaches end index, % wraps back to start
index_of_word = num_of_days(start_date, today) % len(word_list)

# sanity check the length of the chosen wordle word to be 5
if len(word_list[index_of_word]) != 5:
    print("illegal word")
else:
    # prints wordle Yesterday, Today, Tomorrow
    print("Yesterday:\t\t" + word_list[index_of_word - 1])
    print("Today:\t\t\t" + word_list[index_of_word])
    print("Tomorrow:\t\t" + word_list[index_of_word + 1])
