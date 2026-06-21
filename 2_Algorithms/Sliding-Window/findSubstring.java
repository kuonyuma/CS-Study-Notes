class Solution {
    public List<Integer> findSubstring(String s, String[] words) {
        HashMap<String, Integer> hashMap_p = new HashMap<>();
        for(String str : words){
            hashMap_p.compute(str,(k,v)->v == null ? 1:v+1);
        }

        List<Integer> list = new ArrayList<>();
        HashMap<String, Integer> hashMap_s = new HashMap<>();
        int len = words[0].length();
        for(int i = 0;i< len ;i++){
            int count = 0;
            hashMap_s.clear();

            for(int right = i,left = i;right + len <= s.length();right+= len){
                //进窗口
                String str = s.substring(right,right+len);
                hashMap_s.compute(str,(k,v)->v == null ? 1:v+1);
                if(hashMap_p.get(str) != null &&hashMap_s.get(str) <= hashMap_p.get(str)){
                    count += len;
                }
                if((right - left)/len + 1 == words.length){
                    //出窗口
                    if(count == words.length * len){
                        list.add(left);
                    }
                    String str_1 = s.substring(left,left+len);
                    if(hashMap_p.get(str_1)!= null && hashMap_s.get(str_1) <= hashMap_p.get(str_1)){
                        count -= len;
                    }
                    hashMap_s.compute(str_1,(k,v)-> v-1);
                    left += len;
                }
            }
        }
        return list;
    }
}