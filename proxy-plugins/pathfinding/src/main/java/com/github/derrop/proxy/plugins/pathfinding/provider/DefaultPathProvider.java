/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy.plugins.pathfinding.provider;

import com.github.derrop.proxy.api.block.BlockAccess;
import com.github.derrop.proxy.api.location.Location;
import com.github.derrop.proxy.plugins.pathfinding.Path;
import com.github.derrop.proxy.plugins.pathfinding.finder.PathFindInteraction;
import com.github.derrop.proxy.plugins.pathfinding.finder.astar.AStarPathFinder;
import com.github.derrop.proxy.plugins.pathfinding.finder.geometric.CirclePathFinder;
import com.github.derrop.proxy.plugins.pathfinding.finder.geometric.RectanglePathFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultPathProvider implements PathProvider {

    private AStarPathFinder aStarPathFinder = new AStarPathFinder();
    private CirclePathFinder circlePathFinder = new CirclePathFinder();
    private RectanglePathFinder rectanglePathFinder = new RectanglePathFinder();

    @Override
    public Path findShortestPath(@Nullable PathFindInteraction interaction, boolean canFly, @NotNull BlockAccess access, @NotNull Location start, @NotNull Location end) {
        Path path = new Path(start, this.aStarPathFinder.findPath(interaction, access, canFly, start, end));
        if (interaction != null) {
            interaction.complete();
        }
        return path;
    }

    @Override
    public Path findCirclePath(@Nullable PathFindInteraction interaction, @NotNull BlockAccess access, @Nullable Location start, @NotNull Location center, int radius) {
        return null;
    }

    @Override
    public Path findRectanglePath(@Nullable PathFindInteraction interaction, @NotNull BlockAccess access, @Nullable Location start, @NotNull Location pos1, @NotNull Location pos2) {
        Path path = new Path(start, this.rectanglePathFinder.findRectanglePath(access, start, pos1, pos2));
        if (interaction != null) {
            interaction.complete();
        }
        return path;
    }
}
