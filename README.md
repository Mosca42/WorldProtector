# Introduction

WorldProtector is a mod allows you to protect your constructions on your servers. It's based on the same principle as
the plugin WorldGuard but with several differences at command level.

The port of the 1.16.5 version introduces a lot of new flags, new protection features, as well as some QoL improvements.
This mentioned be aware that some of the described features below are not present in every version.

**At this point there is only one active developer working on this mod. Until this changes the **main focus** will be
about maintaining version **1.16.5** and porting the mod to newer Minecraft versions. This means no new features will be
ported back to previous versions, sorry.**

To protect your world you can define regions which can be protected against various actions by adding flags to the
regions. These flags prevent certain actions/events from happening to protect the region: e.g. placing blocks, using
doors or explosions destroying blocks.

This can be done with the Flag Stick in conjunction with the Region Stick or with commands. You can further add players
to a regions which are then allowed to bypass these rules/flags.

## How to create a region?

To define a region, follow these instructions:

1. Use the Region Marker to mark the two blocks which define the area of the region.

2. [Optional] Expand the Y level (height) of your marked region with the ```/wp expand vert [<Y1>] [<Y2>]``` command (
   default is 0 to 255) or by using the secondary function of the Region Marker (see Region Marker tooltip).

3. [Optional] Set the teleportation target of the region with SHIFT right-click on a block. You can teleport to this
   region later by using ```/wp region tp <region>```. When not set, a default teleportation position will be
   calculated.

4. Define your region: ```/wp region define <region>``` or by renaming the Region Marker in an anvil. The regions are
   saved with their name per dimension. This means you cannot have multiple regions with the same name in the same
   dimension. Defining a region with an already used name will override the existing one.

5. Add flags to your region by using the appropriate command ```/wp flag add <region> <flag name>``` or by using the
   Flag Stick in conjunction with the Region Stick (see Flag Stick and Region Stick tooltips).

6. [Optional] Add players to your region which are allowed to bypass the flags by using the
   command ````/wp player add <region> <playername>```` or by using the Region Stick (see Region Stick tooltip for more
   details).

## Features

### Sticks!

There are three different sticks, which can help you define regions, add and remove protection flags to them, as well as
players. These sticks are extension to the already existing commands the mod offers.

Except for the marking, which has to be done with the Region Marker, it is possible to use this mod only with the
command line. But the most common task can also be done without commands!

Be sure to check out the tooltips of the sticks to get more information!

#### Region Marker (the red one)

The Region Marker allows you to mark an area (cuboid) for which you can then specify a name later(no whitespace
allowed!). To mark a region you simply mark to blocks in the world by clicking on them. Additional you can SHIFT +
right-click to set a teleport target for the region you are marking.

#### Region Stick (the blue one)

The Region Stick is used to select the region you want to edit. This can be done by holding it in your main hand and
left-clicking. With this you are able to cycle through the regions defined in your current dimension.

When selected a region it is possible to add players to the region by first choosing the desired mode (add or remove
player) with SHIFT + right-click, and then hitting a player (don't worry it does not hurt). Added players are not
affected by the flags.

#### Flag Stick (the green one)

The Flag Stick is used to select the flag you want to add/remove to/from a region. This works analog to the region
stick. First choose the corresponding mode (add or remove flag) with SHIFT + right-click, then hold the Region Stick
with the selected region in your offhand, finally hold-right click to apply the flag to the region.

You can also put a bunch of name tags, named with the corresponding flag names in an anvil, into a container and
shift-right click on the container to add or remove all flags in the container at once!
This way you are able to group flags for the same purpose in a container and reuse it when needed. A real time saver!

### Commands

Currently, there are four types of commands to manager regions:

- ```/wp region```: Define, update, remove, activate/deactivate, set region priority; List regions, query information,
  teleport to regions regions;

- ```/wp expand```: allows to modify the Y level (height) of the marked region.

- ```/wp flag```: allows to define one or several flags (rules) for a region to protect it.

- ```/wp player```: allows to add/remove players to/from regions

Use ```/wp help``` for more information. Or ```/wp help <command>``` for information about a specific command.

### Flags

Currently, there are **62 flags** available:

- **break**: prevents players from breaking blocks
- **place**: prevents players from placing blocks
- **ignite-explosives**: prevents explosives from blowing up
- **explosions-blocks**: prevents all explosions from destroying blocks (doesn't work under Sponge)
- **explosions-entities**: prevents all explosions from damaging entities (doesn't work under Sponge)
- **creeper-explosions-blocks**: prevents explosions caused by Creepers to destroy blocks (doesn't work under Sponge)
- **creeper-explosions-entities**: prevents explosions caused by Creepers to damage entities (doesn't work under Sponge)
- **other-explosions-blocks**: prevents all other explosions from destroying blocks (doesn't work under Sponge)
- **other-explosions-entities**: prevents all other explosions from damaging entities (doesn't work under Sponge)
- **tools-secondary**: prevents all type of secondary tool actions (strip wood, till farmland, create paths)
- **strip-wood**: prevents wood from being stripped
- **till-farmland**: prevents farmland from being tilled
- **shovel-path**: prevents creation of path blocks
- **trample-farmland**: prevents all farmland trampling
- **trample-farmland-player**: prevents players from trampling farmland
- **trample-farmland-other**: prevents non-player entities from trampling farmland
- **lightning**: prevents entities being hit by lightning (or at least get hurt/transformed)
- **animal-taming**: prevents players from taming animals
- **animal-breeding**: prevents players from breeding animals
- **animal-mounting**: prevents players from mounting animals
- **spawning-all**: prevents spawning of all entities
- **spawning-monsters**: prevents spawning of monsters
- **spawning-animal**: prevents spawning of animals
- **spawning-irongolem**: prevents spawning of iron golems
- **spawning-xp**: prevents spawning of xp orbs completely
- **use**: prevents players to interact with most blocks like buttons, fences, doors, etc.
- **use-bonemeal**: prevents players from using bone meal
- **access-container**: prevents players from accessing most containers
- **access-enderchest**: prevents players from accessing their ender chest
- **enderpearl-from**: prevents ender pearl teleportation out of a region
- **enderpearl-to**: prevents ender pearl teleportation to a region
- **enderman-teleport-from**: prevents enderman from teleporting out of a region
- **enderman-teleport-to**: prevents enderman from teleporting to a region
- **shulker-teleport-from**: prevents shulkers from teleporting out of a region
- **shulker-teleport-to**: prevents shulkers from teleporting to a region
- **item-drop**: prevents players from dropping items
- **item-pickup**: prevents players from picking up items
- **xp-drop-all**: prevents all entities from dropping xp orbs
- **xp-drop-monster**: prevents monsters from dropping xp orbs
- **xp-drop-other**: prevents non-hostile entities from dropping xp orbs
- **level-freeze**: prevents the player levels from increasing/decreasing (xp orbs will still be picked up)
- **xp-freeze**: prevents the player from gaining xp from xp orbs
- **attack-players**: prevents players from damaging other players (PvP)
- **attack-animals**: prevents players from damaging animals
- **attack-villagers**: prevents players from damaging villagers
- **attack-monsters**: prevents players from damaging monsters
- **invincible**: prevents players from taking damage
- **fall-damage**: prevents entities from taking fall damage
- **fall-damage-players**: prevents players from taking fall damage
- **fall-damage-animals**: prevents animals from taking fall damage
- **fall-damage-villagers**: prevents villagers from taking fall damage
- **fall-damage-monsters**: prevents monsters from taking fall damage
- **send-chat**: prevents players from sending chat messages (doesn't block commands)
- **exec-command**: prevents players from executing commands
- **set-spawn**: prevents players from setting their spawn point
- **sleep**: prevents players from sleeping
- **spawn-portal**: prevents creating of portal blocks by lighting obsidian
- **use-portal**: prevents all entities from using portals (changing dimensions)
- **use-portal-items**: prevents all entities from using portals (changing dimensions)
- **use-portal-players**: prevents players from using portals (changing dimensions)
- **use-portal-monsters**: prevents monsters from using portals (changing dimensions)
- **use-portal-villagers**: prevents villagers from using portals (changing dimensions)
- **use-portal-animals**: prevents animals from using portals (changing dimensions)

You are also able to add or remove all flags by using the special flag 'all': ```/wp flag remove <region> all```

### Region priority / Overlapping regions

If a region is inside another region you have to use the command
```/wp region set-priority <region> <priority>``` to give a specific region more priority over another. The higher the
number, the higher the regions' priority.

Let's suppose that there is a pvp arena in your spawn. Your spawn forbids pvp, but your in your arena you want to have
pvp allowed. Then it's necessary that the arena gains the upper hand over the spawn to activate, if not pvp will not be
possible.

Therefore, the arena region needs to have a higher priority than the spawn region. The following example show how to
achieve this:

```/wp region set-priority spawn 1```

```/wp region set-priority arena 2```

## Additional information

- The mod has to be installed on the client and on the server.
- You can use this mod in a custom modpack.

### Contribution

Found a bug? Or do you have an idea for a new flag or just a general suggestions for the mod?

Don't hesitate to propose them to me. Or even better: Open a
new [issue](https://github.com/Mosca42/WorldProtector/issues)!

### Special thanks

Thanks to BrokenSwing for his help about regions and safeguard in the world.

Thanks to TheBossMax2 for the mod logo.

Thanks to z0rdak for porting the mod to version 1.16.5 and adding new features in the process.

### Links

* [WorldProtector Discord](https://discord.gg/MsA8XPc)
* [WorldProtector on Curseforge](https://www.curseforge.com/minecraft/mc-mods/worldprotector)

### License

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

### Possible future features/improvements

* Flags per dimension
* Blacklist/Whitelist option for regions and dimensions
* Region highlighting
* Flag ideas: set-spawn, sleep-in-bed, use-firework, elytra-fly, ClientChatEvent/CommandEvent
* Configuration support
    * Option to disable specific event checks completely for optimization
    * Default blacklist/whitelist setting for regions
    * Option to disable messages being sent to players
* Permission system
    * Owner for regions
    * Permission levels for region manipulation (creating, deleting, activating, deactivating)
* CLI: make region info commands available for all players
* CLI: add and remove multiple flags at once
* CLI: add option to define a region without the region marker
* Provide simple gui for choosing flag/region, since there could be many to cycle through
* Define any shape as a region