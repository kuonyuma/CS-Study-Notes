class Solution {
    public List<Integer> findAnagrams(String s, String p) {
        HashMap<Character,Integer> hashmap_1 = new HashMap<>();
        HashMap<Character,Integer> hashmap_2 = new HashMap<>();
        List<Integer> list = new ArrayList<>();
        int left = 0,right = 0;

        for(;right < p.length();right++){
            hashmap_1.compute(p.charAt(right),(k,y) -> y == null ? 1: y+1);
        }
        right = 0;

        while(right < s.length()){
            hashmap_2.compute(s.charAt(right),(k,y) -> y == null ? 1: y+1);

            if(right - left + 1 == p.length()){
                if (hashmap_1.equals(hashmap_2)) {
                    list.add(left);
                }
                hashmap_2.compute(s.charAt(left),(x,y) -> y-1);
                if(hashmap_2.get(s.charAt(left)) == 0){
                    hashmap_2.remove(s.charAt(left));
                }
                left++;
            }
            right++;
        }
        return list;
    }
}