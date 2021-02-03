import csv
import yaml
import string


def clean(dtr):
    new = ""
    dtr = dtr.upper()
    dtr = dtr.strip(" ")
    dtr = dtr.replace("  ", " ")
    dtr = dtr.replace(" ", "_")
    alpha = string.ascii_uppercase + "_" + string.digits
    for char in dtr:
        if char in alpha:
            new += char

    return new

def getQuantity(price, default=1000000):
    return int(default / price)

taxRate = 20#%
tax = 1 + (taxRate / 100)

with open(r"src\main\resources\data.csv", "r") as stream:
    with open(r"src\main\resources\materialData.yml", "r") as matStream:
        matData = yaml.load(matStream)
    matKeys = matData.keys()
    unknown = list(matKeys)
    extra = []
    csvData = csv.reader(stream, delimiter=",")

    for name, price, allowed in csvData:
        name = clean(name)
        if name in matKeys and not(price == ""):
            matData[name]["BUY_PRICE"] = float(price) * tax
            matData[name]["SELL_PRICE"] = float(price)
            matData[name]["QUANTITY"] = getQuantity(float(price) * tax)
            matData[name]["ALLOWED"] = bool(allowed)

        elif name in matKeys and price=="":
            matData[name]["BUY_PRICE"] = 0
            matData[name]["SELL_PRICE"] = 0
            matData[name]["QUANTITY"] = 0
            matData[name]["ALLOWED"] = False

        if name in matKeys:
            if name in unknown:
                print(f"Found: {name}")
                unknown.remove(name)

            else:
                print(f"Duplicate found: {name}")

        else:
            extra.append(name)


for key in matData:
    matData[key]["BUY_PRICE"] = round(float(matData[key].get("BUY_PRICE", 0)), 2)
    matData[key]["SELL_PRICE"] = round(float(matData[key].get("SELL_PRICE", 0)), 2)
    matData[key]["QUANTITY"] = int(matData[key].get("QUANTITY", 0))
    matData[key]["ALLOWED"] = bool(matData[key].get("ALLOWED", False))


with open(r"src\main\resources\materials.yml", "w", encoding="utf8") as newStream:
    yaml.dump(matData, newStream)

for name in extra:
    print(f"Misnamed: {name}")

for name in unknown:
    print(f"Missing: {name}")

print(f"""
Found: {len(matData) - len(unknown)}
Misnamed: {len(extra)}
Missing: {len(unknown)}
""")

