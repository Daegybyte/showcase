#!/usr/bin/env python
import py_compile

y = True
while (y):

    print("\nHow much loot are you adding to your purse?\n")

    goldPrice = int(input("Gold: "))
    silverPrice = int(input("Silver: "))
    copperPrice = int(input("Copper "))

    goldPrice = goldPrice*100 #Convert gold to copper
    silverPrice = silverPrice*10 #Convert silver to copper

    print("\nHow much loot do you have already in your purse?\n")
    goldPaid = int(input("Gold: "))
    silverPaid = int(input("Silver: "))
    copperPaid = int(input("Copper "))

    goldPaid = goldPaid*100
    silverPaid = silverPaid*10

    combinedPriceInCopper = (goldPrice + silverPrice + copperPrice)*-1
    combinedPaidInCopper = goldPaid + silverPaid + copperPaid


    if combinedPaidInCopper >= combinedPriceInCopper:
    
        changeDue = combinedPaidInCopper - combinedPriceInCopper

        goldToPlayer = changeDue/100 #decrementation and distribution of coins to player
        changeFromGold = changeDue%100

        silverToPlayer = changeFromGold/10%10
        changeFromSilver = changeFromGold%10

        copperToPlayer = changeFromSilver%10


        print("\nYour new purse total:")
        print (str(int(goldToPlayer))+str("gp")) #casting int to convert coinToPlayer from float
        print (str(int(silverToPlayer))+str("sp"))
        print (str(int(copperToPlayer))+str("cp"))

    else:
        print("\nInsufficient funds!\n")



    programmeContinue = input(str("\nWould you like to add more loot? y/n "))
    if programmeContinue == "y":
        y = True
    elif programmeContinue == "n":
        print ("\nThank you, come again!\n")
        y = False








