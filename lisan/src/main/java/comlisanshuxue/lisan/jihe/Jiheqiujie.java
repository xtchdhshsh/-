//package comlisanshuxue.lisan.jihe;
//
//import comlisanshuxue.lisan.dto.jihedto;
//import io.swagger.annotations.Api;
//import io.swagger.models.auth.In;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//
//@RestController
//@Api(tags = "集合")
//public class Jiheqiujie {
//
//
//    @PostMapping("/jihe")
//    public Set<Integer> jiheyunsuan(@RequestBody jihedto j){
//
//
//
////    @PostMapping("/jihe")
////    public Set<Integer> jiheyunsuan(@RequestBody List<Integer> a,
////                                    @RequestBody List<Integer> b,
////                                    String shizi){
//
//
////        System.out.println("集合");
////        System.out.println("第一个集合："+j.getA());
////        System.out.println("第二个集合："+j.getB());
////        System.out.println(j.getShizi());
////
////        System.out.println("集合");
////        System.out.println("第一个集合："+a);
////        System.out.println("第二个集合："+b);
////        System.out.println(shizi);
//
//
//        Set<Integer> bingji = new HashSet<Integer>();
//        Set<Integer> jiaoji = new HashSet<Integer>();
//        Set<Integer> chaji = new HashSet<Integer>();
//        Set<Integer> duichen = new HashSet<Integer>();
//        if(j.shizi.contains("∪")){
//            bingji.addAll(j.a);
//            bingji.addAll(j.b);
//            return bingji;
//        }
//        else if(j.shizi.contains("∩")){
//            jiaoji.addAll(j.a);
//            jiaoji.retainAll(j.b);
//            return jiaoji;
//        }
////        jiaoji.stream().forEach(s->{
////            System.out.println(s);
////        });
//
////        if(j.shizi.contains("△")){
////            if(j.shizi.charAt(0)=='a'){
////                chaji.addAll(a);
////                chaji.removeAll(b);
////            }else {
////                chaji.addAll(b);
////                chaji.removeAll(a);
////            }
////        }
//
//        else if(j.shizi.contains("-")){
//            if(j.shizi.charAt(0)=='a'){
//                chaji.addAll(j.a);
//                chaji.removeAll(j.b);
//            }else {
//                chaji.addAll(j.b);
//                chaji.removeAll(j.a);
//            }
//            return chaji;
//        }
//
//        else if(j.shizi.contains("△")){
//            duichen.addAll(j.a);
//            duichen.addAll(j.b);
//            Set<Integer> q = new HashSet<>(j.a);
//            q.retainAll(j.b);
//            duichen.removeAll(q);
//            return duichen;
//        }
//
//        return null;
//    }
//}
