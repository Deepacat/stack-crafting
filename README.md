https://curseforge.com/minecraft/mc-mods/stack-crafting-table/ for full mod description

kubejs helper function since im too lazy to add plugin:
```js
function stackShaped(result, pattern, key) {
    for (let i in key) {
        let stack = Item.of(key[i])
        key[i] = {
            item: stack.id.toString(),
            count: stack.count
        }
    }
    result = Item.of(result)
    let recipe = {
        type: "stackcrafting:stack_crafting",
        category: "misc",
        key: key,
        pattern: pattern,
        result: {
            item: result.id,
            count: result.count
        }
    }
    e.custom(recipe)
}

stackShaped("worldshapercore:ulv_miner", [
    "ABC",
    "DE "
], {
    "A": "40x gtceu:wrought_iron_ingot",
    "B": "18x minecraft:copper_ingot",
    "C": "12x gtceu:lead_ingot",
    "D": "32x gtceu:tin_ingot",
    "E": "10x gtceu:rubber_plate"
})
```
Example recipe through pure json/datapacking:
```json
{
  "type": "stackcrafting:stack_crafting",
  "category": "misc",
  "key": {
    "#": {
      "item": "minecraft:iron_ingot",
      "count": 3
    },
    "X": {
      "item": "minecraft:gold_ingot",
      "count": 4
    },
    "Y": {
      "item": "minecraft:dirt",
      "count": 5
    }
  },
  "pattern": [
    "#",
    "X",
    "Y"
  ],
  "result": {
    "count": 7,
    "item": "minecraft:diamond"
  }
}
```
