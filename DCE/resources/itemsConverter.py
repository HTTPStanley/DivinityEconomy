import yaml

def banned(key, bnd=[], match=[]):
    flag = False
    if key in bnd:
        flag = True

    for i in match:
        if i in key:
            flag = True
            break
        
    return flag

def clean(key):
    key = key.replace("  ", " ")
    key = key.replace("_", " ")
    key = key.lower()
    key = key.strip("")
    key = key.strip(" ")
    return key

# load file
with open(r"src\main\resources\items.yml", "r", encoding="utf8") as inFile:
    data = yaml.load(inFile)
    
unique = []
removed = []
# store materials/aliases
materials = {}
aliases = {}

for key in data.keys():
    obj = data[key]
    if type(obj) == str:
        if not(banned(obj.upper())):
            aliases[key] = obj.upper()

        else:
            removed.append(obj)

    elif type(obj) == dict:
        if not(banned(obj["material"].upper())):
            materials[key.upper()] = obj
            aliases[clean(key)] = obj["material"]

        else:
            removed.append(obj)
        

with open(r"src\main\resources\aliases.yml", "w", encoding="utf8") as aliasesFile:
    yaml.dump(aliases, aliasesFile)

with open(r"src\main\resources\materialData.yml", "w", encoding="utf8") as materialsFile:
    yaml.dump(materials, materialsFile)


for i in removed:
    print(i)

print(f"Removed {len(removed)} items")
