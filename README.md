
# Introduction

WorldProtector is a mod allows you to protect your constructions on your servers, 
it's based on the same principle as the plugin WorldGuard but with several differences at command level.

# Features

The Region Stick allows you to mark an area (cuboid) for which you can then specify a name.
For this region it is possible to apply flags that prevent certain actions/events to protect it.


## Commands

Currently, there are three types of commands :

- /wp region: allows to define, edit, remove a region.

- /wp expand: allows to modify the Y level (height) of the marked region.

- /wp flag: allows to define one or several flags (rules) for a region to protect it.

Type /wp help for more information.


## Flags
Currently, there are thirty-two flags :

- break: prevents players from breaking blocks
- place: prevents players (and Endermen) from placing blocks
- use: prevents players to interact with most blocks like buttons, fences, doors, etc.
- chest-access: prevents players from accessing most containers
- enderpearl-from: prevents enderpearl teleportation from region
- enderpearl-to: prevents enderpearl teleportation to region
- tools-secondary: prevents all type of secondary tool actions (strip wood, till farmland, create paths)
- strip-wood: prevents wood from being stripped
- till-farmland: prevents farmland from being tilled
- shovel-path: prevents creation of path blocks
- trample-farmland: prevents all farmland trampling
- trample-farmland-player: prevents players from trampling farmland
- trample-farmland-other: prevents non-player entities from trampling farmland
- mob-spawning-all: prevents all entity spawning.
- mob-spawning-monsters: prevents monsters from spawning
- mob-spawning-animal:  prevents monsters from spawning
- damage-players: prevents players from damaging other players (PvP)
- damage-animals: prevents players from damaging animals
- damage-villagers: prevents players from damaging villagers
- damage-monsters: prevents players from damaging monsters
- item-drop: prevents players from dropping items
- item-pickup: prevents players from picking up items
- exp-drop: prevents dropping of experience orbs
- ignite-explosives: prevents explosives from blowing up
- explosions-blocks: prevents all explosions from destroying blocks (doesn't work under Sponge)
- explosions-entities: prevents all explosions from damaging entities (doesn't work under Sponge)
- creeper-explosions-blocks: prevents explosions caused by Creepers to destroy blocks (doesn't work under Sponge)
- creeper-explosions-entities: prevents explosions caused by Creepers to damage entities (doesn't work under Sponge)
- other-explosions-blocks: prevents all other explosions from destroying blocks (doesn't work under Sponge)
- other-explosions-entities: prevents all other explosions from damaging entities (doesn't work under Sponge)
- invincible: prevents players from taking damage
- fall-damage: prevents players from taking damage by fall-damage
- send-chat: prevents players from sending chat messages (doesn't block commands)

You are also able to add or remove all flags by using the special flag 'all'.

## Create a region

To create your region, follow these instructions :

1. Use the region stick to mark the two blocks which define the area of the region

2. [Optional] Expand the Y level (height) of your marked region with the ```/wp expand vert [<Y1>] [<Y2>]``` command (default is 0 to 255).

3. Define your region: ```/wp region define <your region's name>```

4. Add flags to your region: ```/wp flag add <your region's name> <flag name>```


## Region priority

If a region is inside another region you have to use the command 
```/wp region setpriority <your region's name> <priority (1,2,3,4,5,6...)>``` to give a specific region more priority over another.

Let's suppose that there is a pvp arena in your spawn. Your spawn forbids pvp, but your in your arena you want to have pvp allowed.
Then it's necessary that the arena gains the upper hand over the spawn to activate, if not pvp will not be possible.

Therefore, the arena region needs to have a higher priority than the spawn region. The following example show how to achieve this:

```/wp region setpriority spawn 1```

```/wp region setpriority arena 2```

2 is superior to 1, so that the arena gains the upper hand over the spawn region and PvP is possible.

# Additional information

- The mod has to be installed on the client and on the server too
- You can use this mod in a custom modpack

# Contribution

Found a bug? Or do you have an idea for a new flag or jsut a general suggestions for the mod?

Don't hesitate to propose them to me. Or even better: Open a new [issue](https://github.com/Mosca42/WorldProtector/issues)!


# Special thanks

Thanks to BrokenSwing for his help about regions and safeguard in the world.

Thanks to TheBossMax2 for the mod logo.

DISCORD: [WorldProtector Discord](https://discord.gg/MsA8XPc)

# License

This mod is open sourced under the Creative Commons 3.0 Attribution Non-Commercial License
https://creativecommons.org/licenses/by-nc/3.0/legalcode

Summary
https://creativecommons.org/licenses/by-nc/3.0/

You are free to:
* Share — copy and redistribute the material in any medium or format
* Adapt — remix, transform, and build upon the material
* The licensor cannot revoke these freedoms as long as you follow the license terms.

Under the following conditions:
* Attribution — You must give appropriate credit, provide a link to the license, and indicate if changes were made. You may do so in any reasonable manner, but not in any way that suggests the licensor endorses you or your use.
* NonCommercial — You may not use the material for commercial purposes.
* No additional restrictions — You may not apply legal terms or technological measures that legally restrict others from doing anything the license permits.

# Planned features

*  Define any shape as a region
*  Region highlighting
*  Compass support for regions
*  Command line QoL - add all, remove all flags, tp link
*  Command to set default expand vert values for region stick
*  Configuration 
*  CLI flags per dimension - Save flags per dimension in overworld nbt
*  Region claiming - Maybe with additional subregions for players 