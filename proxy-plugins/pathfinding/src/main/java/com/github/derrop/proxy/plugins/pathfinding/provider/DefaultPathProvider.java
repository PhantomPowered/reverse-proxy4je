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
import com.github.derrop.proxy.api.location.BlockPos;
import com.github.derrop.proxy.plugins.pathfinding.Path;
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
    public Path findShortestPath(@NotNull BlockAccess access, @NotNull BlockPos start, @NotNull BlockPos end) {
        return new Path(start, this.aStarPathFinder.findPath(access, start, end));
    }

    @Override
    public Path findCirclePath(@NotNull BlockAccess access, @Nullable BlockPos start, @NotNull BlockPos center, int radius) {
        return null;
    }

    @Override
    public Path findRectanglePath(@NotNull BlockAccess access, @Nullable BlockPos start, @NotNull BlockPos pos1, @NotNull BlockPos pos2) {
        return new Path(start, this.rectanglePathFinder.findRectanglePath(access, start, pos1, pos2));
    }
}
