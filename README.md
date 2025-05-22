# Multi-Agent System Auction Project

## Requirements

- Java 8+
- JADE Framework (included in lib/jade.jar)

## Running the Project

### Part 1: Basic Auction

```
# Using Gradle
./gradlew run

# OR manually
java -cp lib/jade.jar:build/classes/java/main jade.Boot -gui -agents "SellerAgent:part1.SellerAgent(100,130);BuyerAgent1:part1.BuyerAgent(150);BuyerAgent2:part1.BuyerAgent(180);BuyerAgent3:part1.BuyerAgent(200)"
```

### Part 2: Enhanced Auction

```
# Start main platform with agents
java -cp lib/jade.jar:build/classes/java/main jade.Boot -gui -agents "Seller1:part2.SellerAgent(Electronics);Seller2:part2.SellerAgent(Books);Seller3:part2.SellerAgent(Clothing);MobileBuyer:part2.MobileBuyerAgent(SubContainer)"

# In another terminal, create a second container for agent migration
java -cp lib/jade.jar jade.Boot -container -container-name SubContainer
```

### Part 3: Jupyter Notebook

```
cd part3
jupyter notebook planning_partial_order_planner.ipynb
```

## Agent Parameters

### Part 1

- SellerAgent: (startingPrice, reservePrice)
- BuyerAgent: (maxBidValue)

### Part 2

- SellerAgent: (productCategory)
- MobileBuyerAgent: (targetContainerForMigration)
