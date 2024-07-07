![Spiget Downloads](https://img.shields.io/spiget/downloads/102242?style=for-the-badge)
![Spiget Rating](https://img.shields.io/spiget/rating/102242?style=for-the-badge)
![Spiget tested server versions](https://img.shields.io/spiget/tested-versions/102242?style=for-the-badge)

![image](https://user-images.githubusercontent.com/29258035/208372157-7ebad587-6c32-493f-8f45-4786432db824.png)

## Introduction

TheGoldEconomy is a gold based Economy plugin that's lightweight, easy to configure and just works.

## Table of Contents

<!--ts-->

- [Features](#features)
- [Usage](#usage)
- [How to Install](#how-to-install)
- [Commands](#commands)
- [Permissions](#permissions)
- [Placeholders](#placeholders)
- [Config](#config-file)
<!--te-->

## Features

- Easy to use.
- Use nuggets or ingots as your base!
- PlaceholderAPI Integration!
- Towny and Vault support.
- TownyAPI Integration!
- Optional removing of Gold Drops by Mobs to reduce inflation.
- Multiple Language support!

## How to Install

- Install Vault
- Download TheGoldEconomy jar and put it into plugins folder.
- Enjoy playing!

## Commands

### Player

- **/bank balance**  
  view your Balance.
- **/bank deposit <gold>**  
  Deposits gold from you inventory into your account.
- **/bank withdraw <gold>**  
  Withdraws gold from you account into your inventory.
- **/bank pay <player> <gold>**  
  Pay a player gold.

## Permissions:

- `thegoldeconomy.set`  
  /bank set
- `thegoldeconomy.add`  
  /bank add
- `thegoldeconomy.remove`  
  /bank remove

## Placeholders

The following placeholders are available if using PlaceholderAPI

- `thegoldeconomy_inventoryBalance`
- `thegoldeconomy_bankbalance`
- `thegoldeconomy_totalBalance`

## Config File:

```
# Remove Gold Drops from Mobs like Piglins? (default: true)
removeGoldDrop: true
# Should the plugin check for updated? (default: true)
updateCheck: true
# Valid language are:
# German: de_DE
# English: en_US
# Spanish: es_ES
# Simplified Chinese: zh_CN
# Turkish: tr_TR
language: "en_US"
# Do you want to restrict bank commands to bank plots (requires Towny)
restrictToBankPlot: false
# Prefix
prefix: "TheGoldEconomy"
# This value sets the base domination of the economy
# 'nuggets' = 1 nugget is 1 currency, 1 ingot is 9, 1 block is 81
# 'ingots'  = 1 ingots is 1 currency, 1 block is 9
base: "nuggets"
```
