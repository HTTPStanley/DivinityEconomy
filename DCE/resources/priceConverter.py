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

def userify(dtr):
    dtr = dtr.lower()
    dtr = dtr.strip(" ")
    dtr = dtr.replace("_", " ")
    dtr = dtr.title()
    return dtr

def getQuantity(price, default=1000000):
    if price > 0:
        return int(default / price)
    else:
        return 0

with open(r"src\main\resources\data.csv", "r") as stream:
    with open(r"src\main\resources\materialData.yml", "r") as matStream:
        matData = yaml.load(matStream)
    matKeys = matData.keys()
    unknown = list(matKeys)
    extra = []
    csvData = csv.reader(stream, delimiter=",")

    for name, price, allowed in csvData:
        name = clean(name)
        if name in matKeys:
            price = 0 if price == "" else price
            matData[name]["QUANTITY"] = getQuantity(float(price))
            matData[name]["ALLOWED"] = bool(True if allowed == "TRUE" else False)

            if name in unknown:
                print(f"Found: {name}")
                unknown.remove(name)

            else:
                print(f"Duplicate found: {name}")

        else:
            extra.append(name)


for key in matData:
    matData[key]["QUANTITY"] = int(matData[key].get("QUANTITY", 0))
    matData[key]["ALLOWED"] = bool(matData[key].get("ALLOWED", False))
    matData[key]["CLEAN_NAME"] = str(userify(key))
    matData[key]["MATERIAL"] = str(matData[key].get("material", key))
    matData[key]["POTION_DATA"] = matData[key].get("potionData", None)
    matData[key]["ENTITY"] = matData[key].get("entity", None)
    if "fallbacks" in matData[key]:
        matData[key].pop("fallbacks")
    if "potionData" in matData[key]:
        matData[key].pop("potionData")
    if "material" in matData[key]: 
        matData[key].pop("material")
    if "entity" in matData[key]: 
        matData[key].pop("entity")


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

