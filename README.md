# statvoxel
"things far away occur less often but are the same size as things near" and less directly relevant to this project "I have tamed the beast of lambda so it will no longer bite us with halting problem though mathematically it is still possible of a vanishingly small chance, it is now friendlier than a cat"

The goal of this project is to randomly select from near voxels, by a chosen nonlinear function (such as linear or squared), to display a small near subset of 1 gigavoxel per video frame per computer, potentially in a space of many more voxels from distant/multiplayer sources but want to get it working on 1 computer first.

I need voxel graphics (for a proof of concept of lambdasmiter, that you might experience a little similar to some old 3d game, but with drag-and-droppable functions that you drag function onto function to find/create function, as needed but more often just chill out in the 3d space or as many dimensions as you like, but just 3d and near endless and turing complete possibilities), nothing fancy (of the graphics), not even high resolution or competitive with the newest games. But I want to be able to imagine and play with huge worlds (especially where we may all paint lambda functions (any turing complete possible behaviors) within such a volume of possible spaces, as I have tamed the beast of lambda so it will no longer bite us with halting problem though mathematically it is still possible of a vanishingly small chance, it is now friendlier than a cat and has far more potential uses), of static (constant position and color) voxels at each place and time. If each voxel is an int64 (aka long) and there are 100000 voxels displayed at once, then that would be long[100000], of maybe 1 billion longs available to display. Lets call that long[d] sortedVoxelsToDisplay. Theres some function(number)->number that maps any voxel into an index in sortedVoxelsToDisplay. The farther away it is, the higher the index in sortedVoxelsToDisplay. Newtonian gravity is by inverse distance squared cuz surface area of a sphere increases by square of the distance, like volume increases by cube of distance. In the most basic case, the function is linear (exponent 1) of the distance from viewing position, so sortedVoxelsToDisplay[500] is 500 times some constant distance from here, and sortedVoxelsToDisplay[501] is 501 times some constant distance from here, and so on, but it doesnt have to be linear. Its probably best to be linear so the number of voxels displayed is viewDepth times area of the screen, instead of increasing beyond screen area, or maybe even less than linear, but linear seems to be the best balance, in theory.

From such statistical distribution of, for example a billion longs/voxels (loaded locally into memory, of potentially a much larger set of voxels such as 3d voxels of the whole planet, but lets keep it simple for now, on the scale of games you play locally)... From that a voxel is chosen at random, that tends to be near the viewing position. The mutable sortedVoxelsToDisplay[] contains for example 100000 voxels. The incoming voxel's distance from this viewing position is translated (by some "nonlinear function (such as linear or squared)") to a specific integer index in sortedVoxelsToDisplay, which it overwrites the long (or however many bits the voxel has, such as long), then repeat many times. After that happens many times, 1/60 second has passed (or however often you like your FPS, to balance between number of voxels, resolution, and lag)...

Then the display happens. Reusing an existing array such as java BufferedImage of 1024x1024, or whatever resolution you want to pay computing resources for (which is stretchable more efficiently than directly writable due to higher level memory IO limits)... Loop from high to low index in sortedVoxelsToDisplay, and display each voxel as a 2d square (in the simplest case, or circles might look better) of some small constant 2d size, of its x y z translated to screenX and screenY, and constant size on screen (regardless of distance, so things far away occur less often but are the same size as things near, and tend to have things nearer displayed in front of them), display it in that color, then continue the loop toward lower index aka voxels nearer. Then repeat the whole process approx 1/60 second later from a different viewing position and different set of voxels which some may have been added and some removed from the for example 1 billion voxels in this computer's memory.

This is just a very very basic model of voxel graphics, which could later maybe have bumpmapping added (per voxel, 1 bump each, like opengl light sources (which I could easily compute 300 moving variable color light sources in realtime to all 100000 bumps/voxels using opencl ndrange kernel, BUT NO SHADOWS cuz those are far more expensive more similar to raytracing), but for now is just a color at a 3d position, going for brute force of very very many of them displayed at once). It would look a little like fizzling heat appearing and disappearing, or radio static or a tv tuned to no signal, a little, but mostly converge to a stable image of high detail. I'm not saying its great graphics, but it would allow me and maybe others who want to explore this kind of possible tool, to paint things we imagine, to explore quickly without much effort, things we might imagine and have other tools generate, without needing large complex tools. It would be something thats just a few kilobytes of code plus java BufferedImage or js html canvas or whatever other system it might be ported to. Its a quick and easy way to, in theory, navigate a space of a billion cartoony local voxels. That is, if can get the statistics to select from those voxels that happen to be near a chosen 3d position, in proportion to the "chosen nonlinear function (such as linear or squared)" (by default, each linear distance from here is equally likely as every other linear distance, so you should get about the same number of voxels randomly selected at distance 500 as distance 5000 and distance 501 and distance 5001, for example. Its just something really simple to start with.

TODO...
