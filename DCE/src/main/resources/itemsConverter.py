import yaml, tqdm

# load file
with open("items.yml", "r", encoding="utf8") as inFile:
    data = yaml.load(inFile)
    
unique = []
# store materials/aliases
materials = {}
aliases = {}

for key in tqdm.tqdm(data.keys(), desc="Converting..."):
    obj = data[key]
    if type(obj) == str:
        aliases[key] = obj.upper()

    elif type(obj) == dict:
        materials[key.upper()] = obj

with open("aliases.yml", "w", encoding="utf8") as aliasesFile:
    yaml.dump(aliases, aliasesFile)

with open("materials.yml", "w", encoding="utf8") as materialsFile:
    yaml.dump(materials, materialsFile)
