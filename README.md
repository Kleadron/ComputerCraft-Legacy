# ComputerCraft-Legacy
A reverse engineering and backport of ComputerCraft 1.21 for Legacy Minecraft.

## Current Status
Computers are now able to interact correctly with redstone, redpower red alloy wires and bundled cables!
Computer world saving is working.

Disk drives and disks work! Unfortunately because Item.addInformation doesn't exist in this version, you won't be able to see the disk's label from your inventory :( 

Computer recipe changed from glass pane to glass block.

Multiplayer does not work yet, and a lot of multiplayer code was stripped out for simplicity but will be re-added back.

Config is a mystery and I will have to figure it out.

## How to build
You will need a working MCP v4.3 environment with modloader and modloadermp installed.

To see dan200 and org.luaj.vm2, under Client in the Package Explorer, right click "src" and click properties. Go to resource > resource filters, remove the resource filter that says "Name matches net"

## Links
This is licensed under the same license as seen at https://github.com/dan200/ComputerCraft

Resources used: 

https://github.com/FabricMC/Enigma

https://minecraft.gamepedia.com/Programs_and_editors/Mod_Coder_Pack

CC classes are manually remapped using mapping files from MCP v5.6

Web archive link to the minecraft forum page with CC 1.21:
http://web.archive.org/web/20120208211521/http://www.minecraftforum.net/topic/892282-11-computercraft-121/


