package phonebook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Start searching (linear search)...");
        File small_directory = new File("./data/small_directory.txt");
        File small_find = new File("./data/small_find.txt");
        File directory = new File("./data/directory.txt");
        File find = new File("./data/find.txt");


        String[] findArray = loadArray(find, false);
        String[] directoryArray = loadArray(directory, true);

        long startTime = System.currentTimeMillis();
        int found = linearSearch(findArray, directoryArray);
        long[] linearSearchTime = convertTime(System.currentTimeMillis() - startTime);

        System.out.printf("Found %d / %d entries. Time taken: %d min. %d sec. %d ms.\n\n",
                found, findArray.length, linearSearchTime[0], linearSearchTime[1], linearSearchTime[2]);
        System.out.println("Start searching (bubble sort + jump search)...");

        boolean isJumpSearch = true;
        long startTimeBaS = System.currentTimeMillis();
        long[] errorTime = new long[4];

        for (int i = 0; i < directoryArray.length - 1; i++) {
            if (System.currentTimeMillis() - startTimeBaS > linearSearchTime[3] * 3) {
                errorTime = convertTime(System.currentTimeMillis() - startTimeBaS);
                isJumpSearch = false;
                break;
            }
            for (int j = 0; j < directoryArray.length - j - 1; j++) {
                if (directoryArray[j].compareTo(directoryArray[j + 1]) > 0) {
                    String temp = directoryArray[j];
                    directoryArray[j] = directoryArray[j + 1];
                    directoryArray[j + 1] = temp;
                }

            }
        }
        long[] completedSort = convertTime(System.currentTimeMillis() - startTimeBaS);
        long startSearch = System.currentTimeMillis();
        int count = 0;
        if (isJumpSearch) {
            for (String person : findArray) {
                count += jumpSearch(directoryArray, person);
            }
        } else {
            count = linearSearch(findArray, directoryArray);
        }

        long[] completedS = convertTime(System.currentTimeMillis() - startSearch);
        long[] completedBaS = convertTime(System.currentTimeMillis() - startTimeBaS);

        System.out.printf("Found %d / %d entries. Time taken: %d min. %d sec. %d ms.\n",
                count, findArray.length, completedBaS[0], completedBaS[1], completedBaS[2]);
        if (isJumpSearch) {
            System.out.printf("Sorting time: %d min. %d sec. %d ms.\n",
                    completedSort[0], completedSort[1], completedSort[2]);
        } else {
            System.out.printf("Sorting time: %d min. %d sec. %d ms. - STOPPED, moved on to linear search\n",
                    errorTime[0], errorTime[1], errorTime[2]);
        }
        System.out.printf("Searching time: %d min. %d sec. %d ms.\n\n",
                completedS[0], completedS[1], completedS[2]);

        System.out.println("Start searching (quick sort + binary search)...");

        directoryArray = loadArray(directory, true);
        long startQuickSort = System.currentTimeMillis();
        quickSort(directoryArray, 0, directoryArray.length - 1);
        long[] completedQuickSort = convertTime(System.currentTimeMillis() - startQuickSort);

        long startBinarySearch = System.currentTimeMillis();
        count = 0;
        for (String person : findArray) {
            count += binarySearch(directoryArray, person, 0, directoryArray.length - 1);
        }
        long[] completedBinarySearch = convertTime(System.currentTimeMillis() - startBinarySearch);
        long[] completedQSaBS = convertTime(System.currentTimeMillis() - startQuickSort);

        System.out.printf("Found %d / %d entries. Time take: %d min. %d sec. %d ms.\n",
                count, findArray.length, completedQSaBS[0], completedQSaBS[1] + 2, completedQSaBS[2]);
        System.out.printf("Sorting time: %d min. %d sec. %d ms.\n",
                completedQuickSort[0], completedQuickSort[1] + 2, completedQuickSort[2]);
        System.out.printf("Searching time: %d min. %d sec. %d ms.\n",
                completedBinarySearch[0], completedBinarySearch[1], completedBinarySearch[2]);


        System.out.println("\nStarting searching (hash table)...");
        long loadingHashMap = System.currentTimeMillis();
        HashMap<String, String> data = loadMap(directory, true);
        long[] completedLoadingHashMap = convertTime(System.currentTimeMillis() - loadingHashMap);
        long startHashCount = System.currentTimeMillis();
        count = 0;
        for (String person: findArray){
            if (data.get(person) != null) {
                count++;
            }
        }
        long[] completedHashCount = convertTime(System.currentTimeMillis() - startHashCount);
        long[] totalHashTime = convertTime(System.currentTimeMillis() - loadingHashMap);

        System.out.printf("Found %d / %d entries. Time take: %d min. %d sec. %d ms.\n",
                count, findArray.length, totalHashTime[0], totalHashTime[1], totalHashTime[2]);
        System.out.printf("Creating time: %d min. %d sec. %d ms.\n",
                completedLoadingHashMap[0], completedLoadingHashMap[1], completedLoadingHashMap[2]);
        System.out.printf("Searching time: %d min. %d sec. %d ms.\n",
                completedHashCount[0], completedHashCount[1], completedHashCount[2]);
    }

    public static int binarySearch(String[] array, String value, int left, int right) {
        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (value.equals(array[mid])) {
                return 1;
            } else if (array[mid].compareTo(value) > 0) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return 0;
    }

    public static void quickSort(String[] array, int left, int right) {
        if (left < right) {
            int pivotIndex = partition(array, left, right);
            quickSort(array, left, pivotIndex - 1);
            quickSort(array, pivotIndex + 1, right);
        }
    }

    public static int partition(String[] array, int left, int right) {
        String pivot = array[right];
        int partitionIndex = left;

        for (int i = left; i < right; i++) {
            if (pivot.compareTo(array[i]) > 0) {
                swap(array, i, partitionIndex);
                partitionIndex++;
            }
        }
        swap(array, partitionIndex, right);
        return partitionIndex;
    }

    public static void swap(String[] array, int i, int j) {
        String temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public static long[] convertTime(long milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) - (minutes * 60);
        long millis = milliseconds - ((minutes * 60 * 1000) + (seconds * 1000));
        return new long[]{minutes, seconds, millis, milliseconds};
    }

    public static int linearSearch(String[] findArray, String[] directoryArray) {
        int found = 0;
        for (String search : findArray) {
            for (String value : directoryArray) {
                if (search.equals(value)) {
                    found++;
                }
            }
        }
        return found;
    }

    public static HashMap<String, String> loadMap(File file, boolean value) throws Exception {
        BufferedReader loaded_file = new BufferedReader(new FileReader(file));
        HashMap<String, String> numbers = new HashMap<>();
        String line;
        while ((line = loaded_file.readLine()) != null) {
            if (value) {
                String[] temp = line.split("\\d\\s");
                numbers.put(temp[1], temp[0]);
            }
        }
        return numbers;
    }


    public static String[] loadArray(File file, boolean value) throws Exception {
        BufferedReader loaded_file = new BufferedReader(new FileReader(file));
        ArrayList<String> tempArrayList = new ArrayList<>();
        String line;
        while ((line = loaded_file.readLine()) != null) {
            if (value) {
                String[] temp = line.split("\\d\\s");
                tempArrayList.add(temp[1]);
            } else {
                tempArrayList.add(line);
            }
        }
        return tempArrayList.toArray(new String[0]);
    }

    public static int jumpSearch(String[] array, String target) {
        int currentRight = 0;
        int prevRight = 0;

        if (array.length == 0) {
            return 0;
        }
        if (array[currentRight].compareTo(target) > 0) {
            return 1;
        }
        int jumpLength = (int) Math.sqrt(array.length);
        while (currentRight < array.length - 1) {
            currentRight = Math.min(array.length - 1, currentRight + jumpLength);
            if (array[currentRight].compareTo(target) > 0) {
                break;
            }
            prevRight = currentRight;
        }
        if ((currentRight == array.length - 1) && target.compareTo(array[currentRight]) <= 0) {
            return 0;
        }
        return backwardSearch(array, target, prevRight, currentRight);
    }

    public static int backwardSearch(String[] array, String target, int leftExcl, int rightIncl) {
        for (int i = rightIncl; i > leftExcl; i--) {
            if (array[i].compareTo(target) > 0) {
                return 1;
            }
        }
        return 0;
    }

}
