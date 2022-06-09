#!/usr/bin/env python
import os
import py_compile

y = True
while (y):
    print("\nWould you like to:\n1. Consolidate your coins?\n2. Purchase an item?\n3. Distribute coins to your party?\n\n")
    programmeChoice = int(input("Select: "))

    if programmeChoice == 1:
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
    
        programmeChoice = input(str("\nWould you like to select another option? y/n "))
        if programmeChoice == "y":
            y = True
        else:
            y = False

    if programmeChoice == 2:
        y = True
        while (y):
            
            sumGoldPrice = 0
            sumSilverPrice = 0
            sumCopperPrice = 0
            
            numberBought = int(input("\nHow many items would you like to buy? "))
            
            for x in range (numberBought):
                print("\nHow much is the item?\n")

                goldPrice = int(input("Gold: "))
                silverPrice = int(input("Silver: "))
                copperPrice = int(input("Copper "))

                sumGoldPrice += goldPrice
                sumSilverPrice += silverPrice
                sumCopperPrice += copperPrice


            goldPrice = sumGoldPrice*100 #Convert gold to copper
            silverPrice = sumSilverPrice*10 #Convert silver to copper
            copperPrice = sumCopperPrice
            
            print("\nHow much did you pay?\n")
            goldPaid = int(input("Gold: "))
            silverPaid = int(input("Silver: "))
            copperPaid = int(input("Copper "))

            goldPaid = goldPaid*100
            silverPaid = silverPaid*10

            combinedPriceInCopper = goldPrice + silverPrice + copperPrice
            combinedPaidInCopper = goldPaid + silverPaid + copperPaid


            if combinedPaidInCopper >= combinedPriceInCopper:
            
                changeDue = combinedPaidInCopper - combinedPriceInCopper

                goldToPlayer = changeDue/100 #decrementation and distribution of coins to player
                changeFromGold = changeDue%100

                silverToPlayer = changeFromGold/10%10
                changeFromSilver = changeFromGold%10

                copperToPlayer = changeFromSilver%10


                print("\nYour change:")
                print (str(int(goldToPlayer))+str("gp")) #casting int to convert coinToPlayer from float
                print (str(int(silverToPlayer))+str("sp"))
                print (str(int(copperToPlayer))+str("cp"))

            else:
                print("\nInsufficient funds!\n")

            programmeContinue = input(str("\nWould you like to purchase an additional item? y/n "))
            if programmeContinue == "y":
                y = True
            elif programmeContinue == "n":
                print ("\nThank you, come again!\n")
                y = False

        programmeChoice = input(str("\nWould you like to select another option? y/n "))
        if programmeChoice == "y":
            y = True
        else:
            y = False
    
    if programmeChoice == 3:
        y = True
        while (y):

            partySize = int(input ("\nHow many players are in your party: "))

            #These are the initial player inputs for the party loot to be divided and then optimised
            print("\nHow much loot is being divided?")
            partyPlatinum = int(input("\nPlatinum: "))
            partyGold = int(input("Gold: "))
            partySilver = int(input("Silver: "))
            partyCopper = int(input("Copper: "))

            platinum = partyPlatinum

            # Gross gold
            grossDividedGold = (partyGold + (platinum*10))
            gold = (int)(grossDividedGold/partySize)
            goldRemaining = grossDividedGold-(partySize*gold)

            # Gross Silver
            grossDividedSilver = (partySilver + (goldRemaining*10))
            silver = (int) (grossDividedSilver/partySize)
            silverRemaining = grossDividedSilver-(partySize*silver)

            # Gross copper
            grossDividedCopper = (partyCopper + (silverRemaining*10))
            copper = (int)(grossDividedCopper/partySize)
            copperRemaining = grossDividedCopper-(partySize*copper)

            print ("\nFor your convenience, platinum has been converted into gold.\n")
            print("Coins per player")
            print(str(int(gold))+str("gp"))
            print(str(int(silver))+str("sp"))
            print(str(int(copper))+str("cp"))
            print(str(int(copperRemaining))+str("cp remaining in party inventory"))

            print("\nOr\n")

            netCopper = copper%10
            copperToSilver = copper /10 #using truncation of ints to get copperToSilver

            grossSilver = copperToSilver + silver 
            netSilver = grossSilver%10
            silverToGold = grossSilver/10

            silverToGold = grossSilver/10
            netGold = gold+silverToGold

            print("Optimised coins per player")
            print(str(int(netGold))+str("gp"))
            print(str(int(netSilver))+str("sp"))
            print(str(int(netCopper))+str("cp"))
            print(str(int(copperRemaining))+str("cp remaining in party inventory"))
            y = False

            programmeContinue = input(str("\nWould you like to continue distributing coins? y/n: "))
            if programmeContinue == "y":
                y = True
            elif programmeContinue == "n":
                print ("\nThank you come again!\n")
                y = False
        programmeChoice = input(str("\nWould you like to select another option? y/n "))
        if programmeChoice == "y":
            y = True
        else:
            y = False
