package edu.unisofia.vmarinov.processing;

import edu.unisofia.vmarinov.hough.HoughCircle;

import java.util.*;

public class StarSequencesProcessor {
    public int[][][] A;
    public List<HoughCircle>[] circles;
    public List<Integer>[][] radiiMap;
    public Set<HoughCircle> visited;

    public StarSequencesProcessor(int[][][] A, List<HoughCircle>[] circles) {
        this.A = A;
        this.circles = circles;
        this.radiiMap = createRadiiMap();
        this.visited = new HashSet<HoughCircle>();
    }

    public int getDistancesMode() {
        int[] distancesArray = new int[A.length], accumulatedArray = new int[A.length];

        for (int i = 0; i < circles[2].size(); i++) {
            for (int j = i + 1; j < circles[2].size(); j++) {
                if (Math.abs(circles[2].get(i).y - circles[2].get(j).y) <= 1) {
                    Integer distance = Math.abs(circles[2].get(i).x - circles[2].get(j).x);
                    distancesArray[distance]++;
                }
            }
        }

        for (int i = 1; i < distancesArray.length - 1; i++) {
            accumulatedArray[i] += distancesArray[i - 1] + distancesArray[i] + distancesArray[i + 1];
        }

        int max = 0, maxIndex = 0;
        for (int i = 0; i < accumulatedArray.length; i++) {
            if (accumulatedArray[i] > max) {
                max = accumulatedArray[i];
                maxIndex = i;
            }
        }

        return maxIndex;
    }

    public List<StarSequence> findStarSequences(int distance) {
        List<StarSequence> result = new ArrayList<StarSequence>();
        Map<HoughCircle, HoughCircle> ancestors = new HashMap<HoughCircle, HoughCircle>();
        Map<HoughCircle, Integer> pathLengths = new HashMap<HoughCircle, Integer>();

        int maxNeighbourhoodSize = 4;
        for(int neighbourhoodSize = 1; neighbourhoodSize <= maxNeighbourhoodSize; neighbourhoodSize++) {
            for (int r = 0; r < A[0][0].length; r++) {
                for (HoughCircle startCircle : circles[r]) {
                    if (visited.contains(startCircle)) {
                        continue;
                    }

                    ancestors.clear();
                    pathLengths.clear();
                    Queue<HoughCircle> sequenceCandidates = new ArrayDeque<HoughCircle>();
                    List<HoughCircle> allCirclesInNeighbourhood = findAllCirclesInNeighbourhood(
                            startCircle.x + 2 * distance,
                            startCircle.y,
                            neighbourhoodSize,
                            startCircle.radius);
                    sequenceCandidates.addAll(allCirclesInNeighbourhood);

                    for (HoughCircle current : allCirclesInNeighbourhood) {
                        ancestors.put(current, startCircle);
                        pathLengths.put(current, 1);
                    }

                    HoughCircle currentCandidate;
                    while(!sequenceCandidates.isEmpty()) {
                        currentCandidate = sequenceCandidates.poll();

                        if (visited.contains(currentCandidate)) {
                            continue;
                        }

                        int currentPathLength = pathLengths.get(currentCandidate);

                        if (currentPathLength == 5) {
                            HoughCircle node = currentCandidate;
                            boolean hasDifferentRadius = false;
                            int maxY = startCircle.y, minY = startCircle.y;

                            while (node != startCircle) {
                                visited.add(node);
                                hasDifferentRadius = hasDifferentRadius || (node.radius != startCircle.radius);

                                if (maxY < node.y) {
                                    maxY = node.y;
                                }
                                if (minY > node.y) {
                                    minY = node.y;
                                }

                                node = ancestors.get(node);
                            }

                            visited.add(startCircle);

                            result.add(
                                    new StarSequence(
                                            startCircle.x - r - 1,
                                            minY - r - 1,
                                            currentCandidate.x - startCircle.x + 2 * r + 1,
                                            2 * r + 1 + (maxY - minY),
                                            r,
                                            hasDifferentRadius ? StarSequence.StarSequenceType.INTERESTING :
                                                    StarSequence.StarSequenceType.NORMAL,
                                            neighbourhoodSize));

                            break;
                        }

                        allCirclesInNeighbourhood = findAllCirclesInNeighbourhood(
                                currentCandidate.x + distance,
                                currentCandidate.y,
                                neighbourhoodSize,
                                startCircle.radius);

                        sequenceCandidates.addAll(allCirclesInNeighbourhood);

                        for (HoughCircle current : allCirclesInNeighbourhood) {
                            ancestors.put(current, currentCandidate);
                            pathLengths.put(current, currentPathLength + 1);
                        }
                    }
                }
            }
        }


        return result;
    }

    private List<HoughCircle> findAllCirclesInNeighbourhood(int x, int y, int neighbourhoodSize, int radius) {
        List<HoughCircle> result = new ArrayList<HoughCircle>();

        for (int i = x - neighbourhoodSize; i <= x + neighbourhoodSize; i++) {
            for (int j = y - neighbourhoodSize; j <= y + neighbourhoodSize; j++) {
                if (i >= A.length || j >= A[0].length || i < 0 || j < 0 || radiiMap[i][j] == null) {
                    continue;
                }

                if (radiiMap[i][j].contains(radius)) {
                    result.add(new HoughCircle(i, j, radius));
                } else if (radiiMap[i][j].contains(radius - 1)) {
                    result.add(new HoughCircle(i, j, radius - 1));
                } else if (radiiMap[i][j].contains(radius + 1)) {
                    result.add(new HoughCircle(i, j, radius + 1));
                }
            }
        }

        return result;
    }

    private List<Integer>[][] createRadiiMap() {
        List<Integer>[][] result = new List[A.length][A[0].length];

        for (int r = 0; r < A[0][0].length; r++) {
            for (HoughCircle circle : circles[r]) {
                if (result[circle.x][circle.y] == null) {
                    result[circle.x][circle.y] = new ArrayList<Integer>();
                }

                result[circle.x][circle.y].add(r);
            }
        }

        return result;
    }
}
