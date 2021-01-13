//package com.sixtofly.chineseaddressanalyzer.temp;
//
//import cn.afterturn.easypoi.excel.ExcelExportUtil;
//import cn.afterturn.easypoi.excel.entity.ExportParams;
//import com.sixtofly.chineseaddressanalyzer.entity.Address;
//import org.apache.poi.ss.usermodel.Workbook;
//
//import java.io.*;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * @author xie yuan bing
// * @date 2020-04-01 11:36
// * @description
// */
//public class InitDataService {
//
//    /**
//     * 直辖市和特别行政区正则表达式
//     */
//    private static final String MUNICIPALITY_NAME_REGEX = "(^上海市|^北京市|^重庆市|^天津市|^澳门特别行政区|^香港特别行政区)(.*)";
//
//    /**
//     * 直辖市和特别行政区正则匹配器
//     */
//    public static final Pattern MUNICIPALITY_NAME_PATTERN = Pattern.compile(MUNICIPALITY_NAME_REGEX);
//
//    public int initAddress() throws IOException {
//        File file = new File("src/main/resources/2020全国行政区划.txt");
//        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
//        String address = null;
//        List<Address> list = new LinkedList<>();
//        Address init = null;
//        while ((address = reader.readLine()) != null) {
//            address = address.trim();
//            init = new Address();
//            init.setCode(address.substring(0, 6));
//            init.setName(address.substring(6).trim());
//            list.add(init);
//        }
//        Address province = new Address();
//        Address city = new Address();
//        boolean municipality = false;
//        Address addr = null;
//        Address pre = null;
//        for (int i = 0; i < list.size(); i++ ) {
//            addr = list.get(i);
//            addr.setId((long) (i + 1));
//            // 第一个
//            if (i == 0) {
//                addr.setParentId(0L);
//                addr.setProvinceName(addr.getName());
//                addr.setProvinceCode(addr.getCode());
//                addr.setLevel("1");
//                province = addr;
//                pre = addr;
//                continue;
//            }
//            // 省份一样
//            if (addr.getCode().substring(0, 2).equals(pre.getCode().substring(0, 2))) {
//                // 市一样
//                if (addr.getCode().substring(0, 4).equals(pre.getCode().substring(0, 4))) {
//                    // 直辖市
//                    Matcher matcher = MUNICIPALITY_NAME_PATTERN.matcher(province.getName());
//                    if (matcher.find()) {
//                        addr.setProvinceName(province.getName());
//                        addr.setProvinceCode(province.getCode());
//                        addr.setParentId(province.getId());
//                    } else {
//                        addr.setProvinceName(province.getName());
//                        addr.setProvinceCode(province.getCode());
//                        addr.setCityName(city.getName());
//                        addr.setCityCode(city.getCode());
//                        addr.setParentId(city.getId());
//                    }
//                    addr.setLevel("3");
//                } else {
//                    // 直辖市
//                    Matcher matcher = MUNICIPALITY_NAME_PATTERN.matcher(province.getName());
//                    if (matcher.find()) {
//                        addr.setProvinceName(province.getName());
//                        addr.setProvinceCode(province.getCode());
//                        addr.setParentId(province.getId());
//                        addr.setLevel("3");
//                    } else {
//                        addr.setProvinceName(province.getName());
//                        addr.setProvinceCode(province.getCode());
//                        addr.setCityName(addr.getName());
//                        addr.setCityCode(addr.getCode());
//                        addr.setParentId(province.getId());
//                        city = addr;
//                        addr.setLevel("2");
//                    }
//                }
//            } else {
//                addr.setProvinceName(addr.getName());
//                addr.setProvinceCode(addr.getCode());
//                addr.setParentId(0L);
//                addr.setLevel("1");
//                province = addr;
//            }
//            pre = addr;
//        }
//        ExportParams params = new ExportParams();
//        Workbook workbook = ExcelExportUtil.exportExcel(params, Address.class, list);
//        File out = new File("F:/temp/address.xls");
//        workbook.write(new FileOutputStream(out));
//        return list.size();
//    }
//
//    public static void main(String[] args) {
//        InitDataService service = new InitDataService();
//        try {
//            service.initAddress();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
