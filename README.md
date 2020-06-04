# ComputerCraft-Legacy
(currently) An experimental, possibly crappy reverse engineering of ComputerCraft 1.2 for Legacy Minecraft.
Currently incomplete.


## Current Status
Computers are nearly complete but do not properly interact with redstone. For some bizzare reason, they can provide power to redpower alloy wires, but still cannot read from them. They properly save to your world folder, but as long as the world name is without any special characters as far as I'm aware. I might be wrong.

Disk drive does not work and will crash the game if you put anything in it, especially anything that isn't a disk. For some reason, the light is red.

Disks are indeterminate, without a disk drive to use them.

Computer recipe changed from glass pane to glass block.

Multiplayer does not work yet, and a lot of multiplayer code was stripped out for simplicity but will be re-added back

## How to build
You will need a working MCP v4.3 environment with modloader and modloadermp installed.

To see dan200 and org.luaj.vm2, under Client in the Package Explorer, right click "src" and click properties. Go to resource > resource filters, remove the resource filter that says "Name matches net"

## Links
This is licensed under the same license as seen at https://github.com/dan200/ComputerCraft

Resources used: 

https://github.com/FabricMC/Enigma

CC classes are manually remapped using mapping files from MCP v5.6

Web archive link to the minecraft forum page with CC 1.21:
http://web.archive.org/web/20120208211521/http://www.minecraftforum.net/topic/892282-11-computercraft-121/


