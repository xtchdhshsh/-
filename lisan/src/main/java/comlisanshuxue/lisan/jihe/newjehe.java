package comlisanshuxue.lisan.jihe;

import comlisanshuxue.lisan.constants.jihechangliang;
import comlisanshuxue.lisan.dto.jihedto;
import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static comlisanshuxue.lisan.constants.jihechangliang.JIHEA;
import static comlisanshuxue.lisan.constants.jihechangliang.JIHEB;


@RestController
@Api(tags = "集合")
public class newjehe {


    @PostMapping("/jihe")
    public Set<Integer> jiheyunsuan(@RequestBody jihedto j){
         List<List<Integer>> lists = new ArrayList<>();
         List<Integer> r = new ArrayList<>();
        if(j.a!=null){
            lists.add(j.a);
            System.out.println("集合a为:"+j.a);
        }
        if(j.b!=null){
            lists.add(j.b);
            System.out.println("集合b为:"+j.b);
        }
        if(j.c!=null){
            lists.add(j.c);
            System.out.println("集合c为:"+j.c);
        }
        String shizi = j.getShizi();
        if(shizi.charAt(0)=='a'){
            r.addAll(lists.get(jihechangliang.JIHEA));
        }else if(shizi.charAt(0)=='b'){
            r.addAll(lists.get(JIHEB));
        }else if(shizi.charAt(0)=='c'){
            r.addAll(lists.get(jihechangliang.JIHEC));
        }
        System.out.println(r);




        for(int i=0;i<shizi.length();i++){
            if(shizi.charAt(i)=='∪'){
                List<Integer> yunsuanjihe = getJihe(lists, shizi.charAt((i + 1)));
                r = getBingji(r, yunsuanjihe);
            }else if(shizi.charAt(i)=='∩'){
                List<Integer> yunsuanjihe = getJihe(lists, shizi.charAt((i + 1)));
                r = getJiaoji(r, yunsuanjihe);
            }else if(shizi.charAt(i)=='△'){
                List<Integer> yunsuanjihe = getJihe(lists, shizi.charAt((i + 1)));
                r = getbuji(r, yunsuanjihe);
            }else if(shizi.charAt(i)=='-'){
                List<Integer> yunsuanjihe = getJihe(lists, shizi.charAt((i + 1)));
                r = getchaji(r, yunsuanjihe);
            }
        }


        Set<Integer> collect = r.stream().collect(Collectors.toSet());
        System.out.println("结果为");
        for (Integer integer : collect) {
            System.out.print(integer);
        }

        return collect;
    }



    public List<Integer> getJihe(List<List<Integer>> lists , char s){
        if(s=='a'){
            return lists.get(jihechangliang.JIHEA);
        } else if (s == 'b') {
            return lists.get(jihechangliang.JIHEB);
        } else if (s == 'c') {
            return lists.get(jihechangliang.JIHEC);
        }
        return null;
    }

    public List<Integer> getBingji(List<Integer> a,List<Integer> b){
        a.addAll(b);
        return a;
    }
    public List<Integer> getJiaoji(List<Integer> a,List<Integer> b){
        a.retainAll(b);
        return a;
    }

    public List<Integer> getchaji(List<Integer> a,List<Integer> b){
        System.out.println(a);
        System.out.println(b);
        a.removeAll(b);
        System.out.println(a);
        return a;
    }
    public List<Integer> getbuji(List<Integer> a,List<Integer> b){
        Set<Integer> s = new HashSet<>(a);
        s.retainAll(b);
        a.addAll(b);
        a.removeAll(s);
        return a;
    }

}
