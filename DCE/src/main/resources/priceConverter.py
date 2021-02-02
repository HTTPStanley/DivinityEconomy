import csv
import yaml
import string


def clean(dtr):
    new = ""
    dtr = dtr.upper()
    dtr = dtr.replace(" ", "_")
    alpha = string.ascii_uppercase + "_"
    for char in dtr:
        if char in alpha:
            new += char

    return new

def getQuantity(price, default=1000000):
    return int(default / price)


with open("data.csv", "r") as stream:
    with open("materials.yml", "r") as matStream:
        matData = yaml.load(matStream)
    matKeys = matData.keys()
    csvData = csv.reader(stream, delimiter=",")

    for name, sell, buy in csvData:
        name = clean(name)
        if name in matKeys and not(buy == "") and not(sell == ""):
            matData[name]["BUY_PRICE"] = float(buy)
            matData[name]["SELL_PRICE"] = float(sell)
            matData[name]["QUANTITY"] = getQuantity(float(buy))


for key in matData:
    matData[key]["BUY_PRICE"] = matData[key].get("BUY_PRICE", 0)
    matData[key]["SELL_PRICE"] = matData[key].get("SELL_PRICE", 0)
    matData[key]["QUANTITY"] = matData[key].get("QUANTITY", 0)


with open("materials2.yml", "w", encoding="utf8") as newStream:
    yaml.dump(matData, newStream)

