import java.util.concurrent.ThreadLocalRandom;

class Solution {

    public void sort(int[] nums, int l, int r) {
        // 1. 递归终止条件：区间无效
        if (l >= r) {
            return;
        }

        // 2. 随机选择基准值
        // 必须先选出一个固定的 pivot 值，而不是在循环中一直引用 nums[t]
        // 因为 nums[t] 的位置在交换过程中值会发生改变
        int randomIndex = ThreadLocalRandom.current().nextInt(l, r + 1);
        int pivot = nums[randomIndex];

        // 3. 三路划分 (Dutch National Flag Algorithm)
        int left = l - 1;   // 小于区边界
        int right = r + 1;  // 大于区边界
        int point = l;      // 当前处理的位置，应该从 l 开始，而不是 0

        while (point < right) {
            if (nums[point] < pivot) {
                // 找到小数，换到左边
                int tmp = nums[left + 1];
                nums[left + 1] = nums[point];
                nums[point] = tmp;
                left++;
                point++;
            } else if (nums[point] > pivot) {
                // 找到大数，换到右边
                int tmp = nums[right - 1];
                nums[right - 1] = nums[point];
                nums[point] = tmp;
                right--;
                // 注意：这里 point 不能自增，因为从右边换回来的数还没经过判断
            } else {
                // 等于基准值
                point++;
            }
        }

        // 4. 递归排序
        // 必须基于当前区间的边界 l 和 r 进行递归，不能写死 0
        sort(nums, l, left);          // 排序左边小数区
        sort(nums, right, r);         // 排序右边大数区
    }

    public int[] sortArray(int[] nums) {
        if (nums == null || nums.length <= 1) return nums;
        // 传入的右边界应该是 length - 1
        sort(nums, 0, nums.length - 1);
        return nums;
    }
}