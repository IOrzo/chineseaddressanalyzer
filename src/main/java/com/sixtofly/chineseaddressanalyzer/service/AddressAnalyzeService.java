package com.sixtofly.chineseaddressanalyzer.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import com.sixtofly.chineseaddressanalyzer.analyzer.AddressAnalyzer;
import com.sixtofly.chineseaddressanalyzer.analyzer.AddressDataSource;
import com.sixtofly.chineseaddressanalyzer.analyzer.impl.LuceneAddressDataSource;
import com.sixtofly.chineseaddressanalyzer.entity.Address;
import com.sixtofly.chineseaddressanalyzer.entity.dto.AddressAnalyzeParams;
import com.sixtofly.chineseaddressanalyzer.entity.dto.AddressDetailDto;
import com.sixtofly.chineseaddressanalyzer.entity.dto.ExpressAddressDto;
import com.sixtofly.chineseaddressanalyzer.mapper.AddressMapper;
import com.sixtofly.chineseaddressanalyzer.util.RegExUtil;
import com.sixtofly.chineseaddressanalyzer.util.SurnameUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xie yuan bing
 * @date 2020-03-27 15:23
 * @description
 */
@Service
@Slf4j
public class AddressAnalyzeService {


//    @Qualifier("mysqlAddressDataSource")
    @Autowired
    private AddressDataSource addressDataSource;


    /**
     * 直辖市和特别行政区正则表达式
     */
    private static final String MUNICIPALITY_NAME_REGEX = "(^上海市|^北京市|^重庆市|^天津市|^澳门特别行政区|^香港特别行政区)(.*)";

    /**
     * 直辖市和特别行政区正则匹配器
     */
    public static final Pattern MUNICIPALITY_NAME_PATTERN = Pattern.compile(MUNICIPALITY_NAME_REGEX);

    /**
     * 省份正则表达式
     */
    private static final String PROVINCE_NAME_REGEX = "(^上海市|^广东省|^河北省|^湖北省|^辽宁省|^北京市|^江西省|^江苏省|^山东省|^甘肃省|^新疆维吾尔自治区|^山西省|^安徽省|^西藏自治区|^云南省|^贵州省|^青海省|^四川省|^内蒙古自治区|^陕西省|^河南省|^澳门特别行政区|^海南省|^广西壮族自治区|^香港特别行政区|^重庆市|^湖南省|^宁夏回族自治区|^浙江省|^天津市|^福建省|^黑龙江省|^吉林省)(.*)";

    /**
     * 省份名称正则匹配器
     */
    private static final Pattern PROVINCE_NAME_PATTERN = Pattern.compile(PROVINCE_NAME_REGEX);


    /**
     * 城市正则表达式
     */
    private static final String CITY_NAME_REGEX = "(^肇庆市|^张掖市|^河池市|^济源市|^阿勒泰地区|^岳阳市|^陇南市|^黄石市|^定西市|^遂宁市|^呼伦贝尔市|^承德市|^淄博市|^玉树藏族自治州|^哈密地区|^贵阳市|^梧州市|^金华市|^徐州市|^四平市|^芜湖市|^十堰市|^菏泽市|^黔西南布依族苗族自治州|^商丘市|^镇江市|^延边朝鲜族自治州|^海北藏族自治州|^昌都地区|^三明市|^喀什地区|^和田地区|^廊坊市|^雅安市|^青岛市|^克拉玛依市|^松原市|^泰州市|^成都市|^齐齐哈尔市|^泸州市|^邯郸市|^湛江市|^宝鸡市|^巴音郭楞蒙古自治州|^日照市|^赣州市|^南宁市|^儋州市|^太原市|^临沂市|^崇左市|^宜春市|^阳泉市|^临夏回族自治州|^泉州市|^牡丹江市|^盘锦市|^固原市|^潍坊市|^阳江市|^孝感市|^凉山彝族自治州|^临汾市|^大庆市|^佛山市|^安庆市|^洛阳市|^安阳市|^嘉兴市|^运城市|^吉林市|^永州市|^鹤岗市|^福州市|^广安市|^西安市|^阜新市|^北海市|^黄山市|^白山市|^汉中市|^衢州市|^枣庄市|^濮阳市|^阿拉善盟|^怀化市|^恩施土家族苗族自治州|^包头市|^南充市|^博尔塔拉蒙古自治州|^东莞市|^泰安市|^无锡市|^衡阳市|^池州市|^眉山市|^乌鲁木齐市|^贺州市|^伊春市|^西宁市|^庆阳市|^吐鲁番地区|^宿迁市|^莆田市|^新乡市|^绍兴市|^甘南藏族自治州|^邵阳市|^延安市|^潮州市|^沧州市|^大同市|^辽阳市|^大兴安岭地区|^驻马店市|^焦作市|^荆州市|^德州市|^海南藏族自治州|^漳州市|^吕梁市|^常州市|^锦州市|^铜仁市|^随州市|^娄底市|^通辽市|^广州市|^昌吉回族自治州|^淮南市|^朝阳市|^武汉市|^南京市|^德阳市|^莱芜市|^平顶山市|^信阳市|^苏州市|^遵义市|^自贡市|^盐城市|^新余市|^黑河市|^宜昌市|^乌海市|^朔州市|^百色市|^株洲市|^保定市|^桂林市|^克孜勒苏柯尔克孜自治州|^佳木斯市|^黄冈市|^辽源市|^周口市|^酒泉市|^渭南市|^阜阳市|^绵阳市|^锡林郭勒盟|^六盘水市|^合肥市|^揭阳市|^安顺市|^海西蒙古族藏族自治州|^台州市|^景德镇市|^哈尔滨市|^日喀则地区|^银川市|^秦皇岛市|^梅州市|^白城市|^郴州市|^绥化市|^吴忠市|^玉林市|^兴安盟|^温州市|^阿里地区|^鸡西市|^白银市|^钦州市|^深圳市|^南阳市|^亳州市|^开封市|^宁德市|^马鞍山市|^抚顺市|^晋中市|^乐山市|^杭州市|^中山市|^达州市|^晋城市|^张家界市|^大连市|^乌兰察布市|^蚌埠市|^聊城市|^嘉峪关市|^石家庄市|^上饶市|^毕节市|^资阳市|^南平市|^漯河市|^西双版纳傣族自治州|^大理白族自治州|^怒江傈僳族自治州|^德宏傣族景颇族自治州|^迪庆藏族自治州|^拉萨市|^南通市|^九江市|^中卫市|^湘潭市|^营口市|^鹤壁市|^攀枝花市|^咸阳市|^柳州市|^黄南藏族自治州|^滁州市|^来宾市|^鹰潭市|^张家口市|^湖州市|^济南市|^塔城地区|^河源市|^宣城市|^铜川市|^扬州市|^厦门市|^赤峰市|^伊犁哈萨克自治州|^平凉市|^清远市|^威海市|^海东地区|^宜宾市|^长春市|^安康市|^舟山市|^阿坝藏族羌族自治州|^唐山市|^连云港市|^山南地区|^鞍山市|^三门峡市|^海口市|^金昌市|^衡水市|^巴中市|^常德市|^七台河市|^巴彦淖尔市|^内江市|^云浮市|^郑州市|^滨州市|^长治市|^葫芦岛市|^商洛市|^那曲地区|^湘西土家族苗族自治州|^天水市|^广元市|^贵港市|^咸宁市|^通化市|^防城港市|^宿州市|^铁岭市|^韶关市|^许昌市|^鄂尔多斯市|^榆林市|^吉安市|^南昌市|^龙岩市|^惠州市|^茂名市|^烟台市|^楚雄彝族自治州|^昆明市|^曲靖市|^昭通市|^玉溪市|^保山市|^丽江市|^红河哈尼族彝族自治州|^临沧市|^普洱市|^林芝地区|^果洛藏族自治州|^黔东南苗族侗族自治州|^襄阳市|^三亚市|^甘孜藏族自治州|^淮安市|^荆门市|^丹东市|^双鸭山市|^邢台市|^忻州市|^江门市|^长沙市|^汕头市|^铜陵市|^武威市|^阿克苏地区|^济宁市|^珠海市|^益阳市|^东营市|^丽水市|^宁波市|^沈阳市|^六安市|^鄂州市|^石嘴山市|^汕尾市|^本溪市|^兰州市|^黔南布依族苗族自治州|^呼和浩特市|^抚州市|^淮北市|^萍乡市|^文山壮族苗族自治州|^乌兰察布盟|^三沙市|^雄安新区)(.*)";

    /**
     * 城市名称正则匹配器
     */
    private static final Pattern CITY_NAME_PATTERN = Pattern.compile(CITY_NAME_REGEX);

    /**
     * 区县市结尾
     */
    private static final String COUNTY_NAME_CITY_REGEX = "^四会市|^灯塔市|^从化市|^胶南市|^市中区|^华阴市|^信宜市|^金坛市|^新乐市|^登封市|^界首市|^兴化市|^同江市|^大冶市|^溧阳市|^永康市|^石狮市|^鹿泉市|^蛟河市|^阿勒泰市|^高要市|^诸暨市|^双辽市|^深州市|^广水市|^江山市|^高平市|^华蓥市|^汨罗市|^根河市|^珲春市|^浏阳市|^恩施市|^九台市|^龙海市|^兰溪市|^广汉市|^罗定市|^铁力市|^三河市|^舒兰市|^句容市|^即墨市|^梅河口市|^雷州市|^天长市|^五大连池市|^德惠市|^合山市|^福安市|^平湖市|^洪湖市|^涟源市|^五指山市|^新市区|^乌兰浩特市|^文登市|^台山市|^宁国市|^磐石市|^扬中市|^英德市|^泰兴市|^兴平市|^牙克石市|^喀什市|^敦煌市|^太仓市|^辉县市|^图们市|^调兵山市|^普兰店市|^阿克苏市|^和龙市|^松滋市|^安陆市|^钟祥市|^霍州市|^灵武市|^锡林浩特市|^桦甸市|^海伦市|^舞钢市|^东港市|^南宫市|^孝义市|^瑞安市|^西市区|^龙口市|^湘乡市|^峨眉山市|^南康市|^贵溪市|^宜兴市|^文昌市|^南市区|^章丘市|^海门市|^额尔古纳市|^公主岭市|^新民市|^天门市|^永安市|^泊头市|^津市市|^乐陵市|^潞城市|^阜康市|^丹江口市|^栖霞市|^富锦市|^昆山市|^偃师市|^库尔勒市|^凤城市|^北市区|^陆丰市|^余姚市|^兴义市|^合作市|^市南区|^武冈市|^孟州市|^绵竹市|^仪征市|^晋江市|^清镇市|^丹阳市|^什邡市|^昌吉市|^高州市|^阆中市|^黄骅市|^定州市|^武穴市|^青铜峡市|^塔城市|^石首市|^盖州市|^和田市|^义乌市|^河间市|^江油市|^卫辉市|^建瓯市|^市北区|^化州市|^宜州市|^满洲里市|^江阴市|^铜仁市|^扎兰屯市|^沁阳市|^仁怀市|^南安市|^汾阳市|^海阳市|^古交市|^海林市|^临湘市|^东阳市|^新沂市|^宁安市|^岑溪市|^桐城市|^韩城市|^敦化市|^耒阳市|^丰城市|^侯马市|^开原市|^集安市|^晋州市|^霸州市|^毕节市|^原平市|^虎林市|^恩平市|^瑞昌市|^新泰市|^荥阳市|^长乐市|^日喀则市|^开平市|^洪江市|^北流市|^兴宁市|^大丰市|^高碑店市|^乳山市|^五家渠市|^东方市|^资兴市|^上虞市|^建阳市|^樟树市|^井冈山市|^常宁市|^哈密市|^万源市|^赤壁市|^宜城市|^莱阳市|^大安市|^寿光市|^漳平市|^邛崃市|^图木舒克市|^肇东市|^义马市|^永城市|^永济市|^高邮市|^武安市|^都江堰市|^都匀市|^龙泉市|^兖州市|^任丘市|^沅江市|^阿尔山市|^宜都市|^石河子市|^巩义市|^老河口市|^海城市|^福鼎市|^兴城市|^建德市|^吉首市|^遵化市|^利川市|^奉化市|^讷河市|^安丘市|^藁城市|^武夷山市|^禹州市|^冀州市|^五常市|^凌源市|^东兴市|^德令哈市|^枝江市|^乐昌市|^赤水市|^新密市|^蓬莱市|^迁安市|^临海市|^常熟市|^个旧市|^开远市|^景洪市|^大理市|^瑞丽市|^二连浩特市|^北安市|^如皋市|^高密市|^双城市|^龙井市|^靖江市|^瑞金市|^凌海市|^沙市区|^莱西市|^霍林郭勒市|^肥城市|^项城市|^乌苏市|^平度市|^连州市|^吴川市|^榆树市|^阳春市|^嵊州市|^应城市|^高安市|^廉江市|^吐鲁番市|^琼海市|^麻城市|^玉门市|^临夏市|^明光市|^延吉市|^曲阜市|^阿图什市|^海宁市|^醴陵市|^乐清市|^伊宁市|^荣成市|^涿州市|^姜堰市|^福清市|^桐乡市|^安国市|^韶山市|^东台市|^西昌市|^林州市|^仙桃市|^当阳市|^辛集市|^吴江市|^格尔木市|^尚志市|^南雄市|^北票市|^温岭市|^昌邑市|^禹城市|^福泉市|^莱州市|^庄河市|^崇州市|^诸城市|^穆棱市|^彭州市|^滕州市|^启东市|^富阳市|^乐平市|^安达市|^洮南市|^邹城市|^慈溪市|^临江市|^张家港市|^阿拉尔市|^介休市|^新郑市|^丰镇市|^临安市|^邳州市|^长葛市|^绥芬河市|^灵宝市|^德兴市|^潜江市|^普宁市|^招远市|^瓦房店市|^凭祥市|^增城市|^临清市|^简阳市|^冷水江市|^胶州市|^北镇市|^宣威市|^安宁市|^楚雄市|^扶余市|^芒市|^阿拉山口市|^巢湖市|^青州市|^鹤山市|^汝州市|^密山市|^桂平市|^万宁市|^沙河市|^大石桥市|^奎屯市|^邓州市|^博乐市|^河津市|^邵武市|^枣阳市|^汉川市|^凯里市|^霍尔果斯市|^铁门关市|^可克达拉市|^双河市|^昆玉市|^靖西市|^盘州市|^彬州市";

    /**
     * 区县区结尾
     */
    private static final String COUNTY_NAME_DISTRICT_REGEX = "^洛江区|^普陀区|^天元区|^丰南区|^开平区|^市中区|^金城江区|^湾里区|^武侯区|^武都区|^东城区|^禹王台区|^金明区|^永定区|^信州区|^香坊区|^海城区|^中山区|^西山区|^云龙区|^防城区|^曲江区|^富拉尔基区|^北辰区|^鼎湖区|^西青区|^西峰区|^崇明区|^镜湖区|^点军区|^南岗区|^涪城区|^伊春区|^雨花区|^西岗区|^金川区|^新洲区|^潘集区|^和平区|^湛河区|^榆阳区|^官渡区|^沧浪区|^淄川区|^茅箭区|^东洲区|^白碱滩区|^经济技术开发区|^谢家集区|^琅琊区|^江夏区|^南山区|^龙华区|^古城区|^新兴区|^零陵区|^牡丹区|^江津区|^南川区|^永川区|^合川区|^经济开发试验区|^阿城区|^呼兰区|^松北区|^桥东区|^丰润区|^黄岩区|^大兴区|^张店区|^中站区|^临淄区|^东兴区|^旌阳区|^上甘岭区|^长寿区|^宿城区|^江汉区|^鲤城区|^独山子区|^茂南区|^平江区|^西湖区|^集宁区|^矿区|^沙河口区|^宝安区|^柯城区|^沿滩区|^汉台区|^二道江区|^茂港区|^双阳区|^爱辉区|^平川区|^连山区|^坊子区|^玉泉区|^运河区|^红旗区|^蓬江区|^贾汪区|^城区|^牟平区|^兴宁区|^晋源区|^鱼峰区|^南浔区|^朝阳区|^新市区|^石景山区|^自流井区|^涪陵区|^太平区|^高港区|^临渭区|^温江区|^黄州区|^桥西区|^双桥区|^河东区|^铁东区|^千山区|^新青区|^盐田区|^五通桥区|^弓长岭区|^青山湖区|^下陆区|^汇川区|^潮南区|^高坪区|^河西区|^蜀山区|^游仙区|^海拉尔区|^雁塔区|^昂昂溪区|^双塔区|^青原区|^长安区|^源汇区|^三元区|^二七区|^天河区|^鸠江区|^秀洲区|^平鲁区|^太和区|^南谯区|^赫山区|^六合区|^张湾区|^泉山区|^广安区|^孝南区|^苏家屯区|^右江区|^尖草坪区|^鄂城区|^梁园区|^下关区|^赤坎区|^萨尔图区|^龙马潭区|^昌邑区|^越城区|^蒸湘区|^大渡口区|^凤泉区|^天桥区|^鹰手营子矿区|^山城区|^崂山区|^石峰区|^团城山开发区|^徽州区|^西市区|^向阳区|^乌达区|^东港区|^汉阳区|^青白江区|^南岔区|^裕华区|^头屯河区|^古塔区|^红古区|^相城区|^屯溪区|^南市区|^大祥区|^金东区|^元宝山区|^椒江区|^东丽区|^福田区|^伍家岗区|^崇川区|^宜秀区|^洛龙区|^灞桥区|^弋江区|^望花区|^越秀区|^七里河区|^龙文区|^奎文区|^滨江区|^琼山区|^道外区|^黄陂区|^惠阳区|^田家庵区|^黄浦区|^延平区|^武江区|^鼎城区|^双台子区|^汉滨区|^路北区|^湘东区|^八公山区|^钢城区|^同安区|^惠农区|^郊区|^工农区|^江南区|^渝中区|^东河区|^禹会区|^绿园区|^北市区|^石鼓区|^江北区|^兴安区|^市南区|^昭阳区|^鼓楼区|^瑶海区|^安宁区|^宁河区|^余杭区|^皇姑区|^端州区|^袁州区|^碾子山区|^建邺区|^黄石港区|^北塘区|^渭滨区|^雨城区|^沈河区|^吉州区|^西安区|^西塞山区|^台儿庄区|^西夏区|^铁山区|^南明区|^解放区|^吴中区|^安州区|^崇安区|^虹口区|^长洲区|^山亭区|^乌尔禾区|^克拉玛依区|^云岩区|^临潼区|^前进区|^峄城区|^天山区|^万秀区|^安居区|^岳阳楼区|^荆州区|^甘州区|^颍泉区|^秀峰区|^临川区|^福山区|^荔湾区|^鄞州区|^庐阳区|^铁西区|^顺城区|^金口河区|^船山区|^东山区|^市北区|^颍东区|^黄山区|^萧山区|^衢江区|^清河区|^夷陵区|^青云谱区|^上街区|^金平区|^钟山区|^任城区|^戚墅堰区|^美兰区|^东昌区|^梅里斯达斡尔族区|^丹徒区|^西固区|^浈江区|^江宁区|^六枝特区|^广阳区|^薛城区|^晋安区|^安次区|^曾都区|^文圣区|^雨花台区|^城关区|^颍州区|^金凤区|^肃州区|^金水区|^雁江区|^江阳区|^巴南区|^红塔区|^老边区|^昌江区|^新林区|^裕安区|^斗门区|^南长区|^大东区|^通川区|^云城区|^龙岗区|^茄子河区|^东胜区|^莱城区|^大武口区|^邗江区|^桃城区|^白云区|^高明区|^梅列区|^澄海区|^丰台区|^闵行区|^港口区|^西城区|^红花岗区|^城阳区|^呼中区|^徐汇区|^龙城区|^海州区|^仁和区|^惠城区|^大港区|^峰峰矿区|^覃塘区|^婺城区|^资阳区|^钦北区|^德城区|^铁锋区|^魏都区|^拱墅区|^平谷区|^金牛区|^港北区|^南岳区|^复兴区|^娄星区|^龙湖区|^汉南区|^迎泽区|^龙沙区|^盘龙区|^文峰区|^城中区|^宝山区|^平山区|^西乡塘区|^良庆区|^邕宁区|^三山区|^陈仓区|^西区|^迎江区|^荷塘区|^老城区|^临翔区|^青山区|^南郊区|^黄埔区|^开发区|^泰山区|^海勃湾区|^江城区|^平桥区|^新城区|^龙子湖区|^米东区|^蚌山区|^卧龙区|^让胡路区|^雁峰区|^船营区|^高新区|^渝水区|^坡头区|^东坡区|^门头沟区|^惠山区|^铁山港区|^川汇区|^白下区|^科尔沁区|^京口区|^城厢区|^秀英区|^尧都区|^长宁区|^龙山区|^旅顺口区|^樊城区|^濠江区|^象山区|^东安区|^武清区|^镇海区|^虎丘区|^河口区|^兴庆区|^宿豫区|^天宁区|^钟楼区|^华龙区|^安定区|^新浦区|^荔城区|^榕城区|^西林区|^章贡区|^枫溪区|^东昌府区|^青羊区|^路桥区|^兰山区|^爱民区|^涧西区|^襄城区|^新抚区|^东西湖区|^细河区|^丰满区|^梅江区|^华容区|^历城区|^北湖区|^白银区|^上城区|^新荣区|^建华区|^红桥区|^北碚区|^回民区|^小河区|^淮阴区|^岳麓区|^溪湖区|^汤旺河区|^蕉城区|^榆次区|^西工区|^碑林区|^金家庄区|^相山区|^贡井区|^金山区|^黄岛区|^南海区|^雁山区|^芦淞区|^白云鄂博矿区|^殷都区|^顺庆区|^瓯海区|^台江区|^新华区|^睢阳区|^盐都区|^番禺区|^美溪区|^芙蓉区|^北关区|^包河区|^岳塘区|^山海关区|^天心区|^万盛区|^乌当区|^昌平区|^嘉陵区|^青浦区|^李沧区|^通州区|^谯城区|^掇刀区|^丛台区|^马尾区|^海曙区|^石龙区|^冷水滩区|^耀州区|^岱岳区|^牧野区|^五营区|^杏花岭区|^海南区|^庐山区|^凉州区|^湖里区|^鸡冠区|^振安区|^江海区|^万柏林区|^于洪区|^滨湖区|^南开区|^玉州区|^宛城区|^九龙坡区|^罗湖区|^滨城区|^隆阳区|^金湾区|^西秀区|^城子河区|^赛罕区|^龙港区|^汉沽区|^北戴河区|^道里区|^钦南区|^雨山区|^蔡甸区|^江干区|^乌伊岭区|^万山区|^润州区|^奉贤区|^芝罘区|^郫都区|^怀柔区|^寒亭区|^港南区|^北林区|^南芬区|^兴山区|^麻章区|^海珠区|^海港区|^洮北区|^龙亭区|^滴道区|^抚宁区|^三水区|^华侨管理区|^东区|^博山区|^新都区|^武陵源区|^马村区|^兴宾区|^卫滨区|^秦都区|^槐荫区|^万州区|^清城区|^港闸区|^渭城区|^白塔区|^驿城区|^宏伟区|^沙市区|^海陵区|^宝坻区|^叙州区|^东风区|^鹤山区|^锡山区|^叠彩区|^阎良区|^新北区|^泉港区|^井陉矿区|^沙依巴克区|^宣州区|^未央区|^周村区|^江岸区|^双滦区|^玄武区|^沙湾区|^兴隆台区|^宝塔区|^历下区|^芗城区|^甘井子区|^丰泽区|^咸安区|^蝶山区|^潮阳区|^闸北区|^龙泉驿区|^新邱区|^桃山区|^津南区|^平房区|^柳北区|^宽城区|^杜集区|^大同区|^浦口区|^印台区|^松山区|^王益区|^金台区|^环翠区|^月湖区|^城西区|^吴兴区|^南沙区|^萝岗区|^尖山区|^大观区|^翠屏区|^珠晖区|^七星区|^秦淮区|^雨湖区|^海淀区|^离石区|^杨浦区|^红山区|^站前区|^广陵区|^双清区|^四方区|^麒麟区|^涵江区|^霞山区|^八步区|^友好区|^义安区|^莱山区|^纳溪区|^龙潭区|^南关区|^湘桥区|^连云区|^阳明区|^莲都区|^花山区|^金安区|^昆都仑区|^立山区|^乌马河区|^塘沽区|^西陵区|^银海区|^石拐区|^定海区|^河北区|^下城区|^盐湖区|^太子河区|^恒山区|^开福区|^北塔区|^邯山区|^香洲区|^松岭区|^顺德区|^黔江区|^洪山区|^振兴区|^金阊区|^顺义区|^江东区|^中原区|^莲湖区|^明山区|^东湖区|^城北区|^松江区|^禅城区|^蓟州区|^苏仙区|^南岸区|^龙安区|^花都区|^亭湖区|^带岭区|^渝北区|^南票区|^浔阳区|^古冶区|^湖滨区|^新罗区|^潍城区|^贵池区|^武进区|^梁子湖区|^大安区|^卫东区|^集美区|^九原区|^朔城区|^武陵区|^栖霞区|^清浦区|^东营区|^长清区|^红星区|^宁江区|^临河区|^路南区|^吉利区|^巴州区|^银州区|^珠山区|^成华区|^麻山区|^北仑区|^顺河回族区|^锦江区|^淇滨区|^安源区|^云溪区|^沈北新区|^秦州区|^麦积区|^江源区|^猇亭区|^南湖区|^利州区|^召陵区|^瀍河回族区|^浉河区|^郾城区|^惠济区|^管城回族区|^东川区|^思茅区|^曹妃甸区|^埇桥区|^昭化区|^达川区|^杨陵区|^江州区|^沾化区|^博望区|^恩阳区|^前锋区|^姑苏区|^观山湖区|^扎赉诺尔区|^浑江区|^淮安区|^滨海新区|^福绵区|^碧江区|^陕州区|^龙圩区|^浑南区|^七星关区|^静安区|^辽中区|^叶集区|^崇文区|^铜山区|^宣武区|^襄州区|^望城区|^红寺堡区|^江都区|^嘉定区|^利通区|^铜官区|^鲅鱼圈区|^罗庄区|^青秀区|^神农架林区|^朝天区|^鹿城区|^源城区|^鹤城区|^淮上区|^秀屿区|^达坂城区|^小店区|^红岗区|^龙凤区|^下花园区|^柳南区|^元宝区|^忻府区|^君山区|^岚山区|^东宝区|^城东区|^凌河区|^金州区|^武昌区|^翠峦区|^水磨沟区|^沙坡头区|^清河门区|^崆峒区|^五华区|^新会区|^翔安区|^二道区|^金山屯区|^山阳区|^大通区|^龙湾区|^四方台区|^加格达奇区|^硚口区|^静海区|^房山区|^原州区|^浦东新区|^岭东区|^沙坪坝区|^思明区|^烈山区|^宣化区|^红海湾经济开发试验区|^仓山区|^商州区|^花溪区|^梨树区|^海沧区|^播州区|^康巴什区|^吉阳区|^天涯区|^崖州区|^坪山区|^莲池区|^海棠区|^平城区|^云州区|^新吴区|^清江浦区|^柯桥区|^柴桑区|^云冈区|^梁溪区|^赣县区|^高昌区|^伊州区|^陵城区|^平桂区|^光明区|^祥符区|^郧阳区|^建安区|^鄠邑区|^开州区|^华州区|^天府新区|^双流区";

    /**
     * 区县县结尾
     */
    private static final String COUNTY_NAME_COUNTY_REGEX = "^武平县|^靖州苗族侗族自治县|^长丰县|^漳县|^水城县|^新建县|^桑日县|^怀远县|^阳谷县|^始兴县|^莒南县|^抚松县|^饶阳县|^光泽县|^繁昌县|^湖口县|^卢氏县|^公安县|^谷城县|^和田县|^宕昌县|^成县|^康县|^文县|^西和县|^两当县|^徽县|^洪泽县|^盐亭县|^巨鹿县|^上高县|^娄烦县|^夏县|^通渭县|^环县|^博野县|^平昌县|^白沙黎族自治县|^曲水县|^东阿县|^蒲县|^仁布县|^昌江黎族自治县|^德格县|^克山县|^红安县|^长泰县|^勃利县|^芷江侗族自治县|^峨边彝族自治县|^莘县|^易县|^庆城县|^紫阳县|^富裕县|^射洪县|^岫岩满族自治县|^五原县|^馆陶县|^郧县|^奉节县|^石渠县|^安义县|^桓台县|^小金县|^永年县|^明溪县|^安乡县|^旺苍县|^砀山县|^乐亭县|^开化县|^冕宁县|^蒲江县|^岱山县|^柳江县|^通江县|^长治县|^开封县|^清流县|^惠东县|^广饶县|^甘孜县|^陵县|^新兴县|^万载县|^类乌齐县|^安仁县|^华池县|^新河县|^定边县|^曹县|^单县|^垣曲县|^隆林各族自治县|^广丰县|^铁岭县|^高青县|^宽甸满族自治县|^大荔县|^天全县|^金阳县|^茌平县|^云霄县|^崇礼县|^宣汉县|^略阳县|^仁化县|^固镇县|^怀仁县|^合浦县|^祁县|^桂东县|^武邑县|^天台县|^息烽县|^嘉鱼县|^疏附县|^仙游县|^赫章县|^三台县|^木垒哈萨克自治县|^盂县|^武陟县|^临洮县|^额敏县|^拜泉县|^安平县|^红原县|^东辽县|^堆龙德庆县|^沁县|^横峰县|^河南蒙古族自治县|^马边彝族自治县|^屯留县|^法库县|^綦江县|^嘉祥县|^泽普县|^威县|^遂溪县|^巫溪县|^新绛县|^义县|^沧县|^石泉县|^寿宁县|^平果县|^雄县|^思南县|^大竹县|^贡觉县|^友谊县|^余江县|^芒康县|^海兴县|^上蔡县|^留坝县|^新干县|^曲沃县|^徐水县|^黟县|^溧水县|^门源回族自治县|^临高县|^岳池县|^天等县|^八宿县|^临夏县|^金秀瑶族自治县|^民权县|^永吉县|^融安县|^嘉荫县|^五莲县|^魏县|^新安县|^洞头县|^沙洋县|^平和县|^扶沟县|^长子县|^云梦县|^惠来县|^东明县|^成武县|^定陶县|^郓城县|^白朗县|^兴文县|^边坝县|^邻水县|^凤山县|^清徐县|^隆昌县|^万年县|^通山县|^青河县|^沙雅县|^叶县|^柳城县|^习水县|^光山县|^威宁彝族回族苗族自治县|^沛县|^宁阳县|^奇台县|^塔河县|^平泉县|^万安县|^睢宁县|^安龙县|^台前县|^松桃苗族自治县|^新邵县|^汉寿县|^肇州县|^白水县|^营山县|^林西县|^泗阳县|^合水县|^尖扎县|^歙县|^闽侯县|^墨竹工卡县|^长阳土家族自治县|^昂仁县|^洛扎县|^疏勒县|^治多县|^龙州县|^临泉县|^梨树县|^龙游县|^广宁县|^辉县市|^龙里县|^镇宁布依族苗族自治县|^府谷县|^苍溪县|^进贤县|^尼玛县|^洛宁县|^广宗县|^宁陕县|^蓬溪县|^沁水县|^龙门县|^肥乡县|^惠安县|^寻乌县|^正安县|^罗甸县|^周宁县|^木兰县|^社旗县|^井陉县|^岢岚县|^扎囊县|^西林县|^依兰县|^全椒县|^和硕县|^封丘县|^沽源县|^广昌县|^满城县|^铜梁县|^都兰县|^怀安县|^寿县|^溆浦县|^平阴县|^徐闻县|^双峰县|^淇县|^肃北蒙古族自治县|^宝兴县|^南溪县|^阿合奇县|^金门县|^广德县|^永泰县|^策勒县|^三原县|^九龙县|^清涧县|^邯郸县|^陆河县|^丰都县|^郸城县|^张北县|^梓潼县|^宜章县|^察布查尔锡伯自治县|^商水县|^积石山保安族东乡族撒拉族自治县|^托里县|^仲巴县|^吉木萨尔县|^长兴县|^迭部县|^肃南裕固族自治县|^赵县|^德江县|^龙南县|^泸县|^罗田县|^灵川县|^于都县|^海晏县|^灵台县|^新津县|^大余县|^湟中县|^资源县|^绥阳县|^费县|^天镇县|^丁青县|^勉县|^芮城县|^阜城县|^萨迦县|^临澧县|^巴里坤哈萨克自治县|^永和县|^泰顺县|^余干县|^封开县|^阳朔县|^寿阳县|^如东县|^来凤县|^慈利县|^昌图县|^上饶县|^云阳县|^金湖县|^顺昌县|^卓资县|^名山县|^天峻县|^泗水县|^靖边县|^揭东县|^皋兰县|^磴口县|^岚皋县|^南江县|^郁南县|^阳高县|^紫云苗族布依族自治县|^新龙县|^长白朝鲜族自治县|^孙吴县|^新田县|^明水县|^炉霍县|^山阳县|^临武县|^彭水苗族土家族自治县|^玉山县|^昌都县|^宁化县|^桐梓县|^利津县|^永春县|^巫山县|^南丹县|^岳阳县|^白玉县|^兰考县|^清新县|^新源县|^宝丰县|^噶尔县|^怀宁县|^连山壮族瑶族自治县|^旌德县|^荔波县|^民勤县|^滨海县|^富平县|^刚察县|^泾县|^澄迈县|^高县|^都安瑶族自治县|^沈丘县|^泰宁县|^富川瑶族自治县|^普兰县|^柳林县|^崇信县|^东至县|^来安县|^江陵县|^乌鲁木齐县|^白河县|^安塞县|^宁津县|^商都县|^兴海县|^壤塘县|^盐边县|^色达县|^象州县|^广平县|^青县|^民和回族土族自治县|^崇仁县|^承德县|^临西县|^玉屏侗族自治县|^长宁县|^原阳县|^临邑县|^汉源县|^饶平县|^团风县|^黑水县|^宾县|^华容县|^通城县|^平武县|^南乐县|^吉水县|^大城县|^谢通门县|^婺源县|^贵德县|^丰县|^鄱阳县|^武宣县|^石柱土家族自治县|^武义县|^加查县|^盘山县|^临潭县|^巩留县|^枞阳县|^华县|^佳县|^高邑县|^林甸县|^曲阳县|^宜川县|^博爱县|^福海县|^若尔盖县|^卓尼县|^嘉善县|^肥西县|^金塔县|^康马县|^靖安县|^湄潭县|^赞皇县|^萨嘎县|^阜新蒙古族自治县|^新乡县|^龙山县|^错那县|^凉城县|^延川县|^鄢陵县|^沂源县|^称多县|^新昌县|^大邑县|^肇源县|^常山县|^清河县|^康平县|^索县|^琼结县|^西乡县|^峡江县|^理塘县|^莒县|^资中县|^大新县|^通河县|^阳新县|^休宁县|^惠水县|^逊克县|^武强县|^城固县|^苍梧县|^兴隆县|^栾城县|^延长县|^沙湾县|^安化县|^吉安县|^伊川县|^揭西县|^盐山县|^剑阁县|^秭归县|^长海县|^宣化县|^永登县|^陵川县|^清丰县|^平乡县|^旬阳县|^连城县|^陇西县|^马尔康县|^房县|^云安县|^余庆县|^仙居县|^松溪县|^囊谦县|^玛纳斯县|^南陵县|^东宁县|^铅山县|^田林县|^鄯善县|^汶上县|^东光县|^漳浦县|^平遥县|^平陆县|^澧县|^平定县|^皮山县|^衡阳县|^黎川县|^郧西县|^安新县|^萧县|^冠县|^美姑县|^大洼县|^滦南县|^奉新县|^藤县|^杂多县|^织金县|^政和县|^大英县|^赣县|^泗县|^浦北县|^博兴县|^永顺县|^涿鹿县|^霍邱县|^花垣县|^忠县|^平坝县|^丰顺县|^永宁县|^乐安县|^石棉县|^苍山县|^长岛县|^米脂县|^平顺县|^磐安县|^蒙山县|^郯城县|^武胜县|^安溪县|^集贤县|^桐柏县|^夏津县|^唐河县|^远安县|^太湖县|^古蔺县|^柏乡县|^宁陵县|^息县|^合江县|^康保县|^镇巴县|^衡南县|^全州县|^太白县|^肥东县|^共和县|^平塘县|^景县|^河曲县|^霍山县|^靖宇县|^辉南县|^宁明县|^昭觉县|^贞丰县|^阳城县|^昌乐县|^麟游县|^邵东县|^博湖县|^浦城县|^修武县|^景宁畲族自治县|^五河县|^蓝山县|^芜湖县|^襄城县|^宜阳县|^容城县|^关岭布依族苗族自治县|^道真仡佬族苗族自治县|^辽阳县|^榆中县|^布拖县|^多伦县|^富蕴县|^方正县|^长武县|^户县|^固阳县|^武隆县|^无棣县|^阳东县|^孟村回族自治县|^贵定县|^突泉县|^清水县|^田阳县|^乾县|^临县|^孝昌县|^隰县|^株洲县|^遂平县|^古县|^赣榆县|^仁寿县|^海原县|^三门县|^分宜县|^平罗县|^华亭县|^稻城县|^新宁县|^和政县|^阜宁县|^温宿县|^将乐县|^衡山县|^尉犁县|^绥中县|^虞城县|^富县|^汤阴县|^吉隆县|^太康县|^隆化县|^延庆县|^开鲁县|^安多县|^乳源瑶族自治县|^那曲县|^灌阳县|^建始县|^阳西县|^罗城仫佬族自治县|^永靖县|^涞水县|^洛南县|^平南县|^民丰县|^澄城县|^沐川县|^灵石县|^沂南县|^越西县|^秀山土家族苗族自治县|^林口县|^汾西县|^淳安县|^正阳县|^镇坪县|^阜南县|^三都水族自治县|^武城县|^保德县|^诏安县|^嵊泗县|^永清县|^竹溪县|^柳河县|^托克逊县|^平山县|^新野县|^克东县|^同德县|^隆安县|^扶绥县|^呼图壁县|^渭源县|^兴安县|^南郑县|^灵丘县|^钟山县|^晴隆县|^农安县|^交城县|^泽州县|^蕉岭县|^沂水县|^邱县|^威远县|^布尔津县|^洋县|^巴塘县|^青川县|^班戈县|^喜德县|^兴仁县|^黔西县|^行唐县|^浠水县|^顺平县|^东丰县|^资溪县|^理县|^缙云县|^苍南县|^若羌县|^雅江县|^丹巴县|^栾川县|^周至县|^千阳县|^桂阳县|^淮滨县|^托克托县|^灵山县|^屏山县|^泸溪县|^武山县|^高阳县|^东海县|^西充县|^和顺县|^旬邑县|^革吉县|^宁城县|^宁都县|^成安县|^吉县|^嘉黎县|^乌兰县|^柘城县|^鹿邑县|^温泉县|^塔什库尔干塔吉克自治县|^隆尧县|^双流县|^永修县|^乌什县|^万荣县|^丹棱县|^泾源县|^富顺县|^博白县|^岚县|^南召县|^内丘县|^连平县|^融水苗族自治县|^蓝田县|^中阳县|^城步苗族自治县|^轮台县|^阳原县|^霍城县|^茶陵县|^鸡东县|^南部县|^蓬安县|^东安县|^天祝藏族自治县|^礼泉县|^彬县|^长垣县|^西吉县|^射阳县|^城口县|^洪洞县|^桓仁满族自治县|^上杭县|^许昌县|^贺兰县|^永昌县|^左权县|^桐庐县|^和静县|^宁南县|^南县|^乾安县|^长顺县|^上栗县|^盐池县|^郏县|^青龙满族自治县|^特克斯县|^稷山县|^秦安县|^霞浦县|^西峡县|^化隆回族自治县|^杞县|^靖远县|^万全县|^鲁山县|^岳西县|^宁武县|^南澳县|^固始县|^睢县|^伽师县|^镇赉县|^武宁县|^东源县|^定南县|^榆社县|^筠连县|^望奎县|^巴东县|^德保县|^抚顺县|^木里藏族自治县|^孟津县|^会同县|^岗巴县|^新和县|^恭城瑶族自治县|^梅县|^康定县|^垫江县|^利辛县|^库车县|^辰溪县|^新蔡县|^容县|^乐都县|^当涂县|^黄龙县|^襄垣县|^盱眙县|^彭泽县|^建平县|^镇安县|^蠡县|^定日县|^云和县|^普格县|^金寨县|^安岳县|^林周县|^松潘县|^措勤县|^鱼台县|^柯坪县|^博罗县|^兴国县|^灌南县|^松阳县|^江孜县|^星子县|^乐业县|^桃江县|^长岭县|^清原满族自治县|^大宁县|^中牟县|^青冈县|^玉环县|^凤县|^内乡县|^阳信县|^舞阳县|^犍为县|^朝阳县|^泰来县|^清水河县|^永定县|^临朐县|^海安县|^聂荣县|^平潭县|^涞源县|^建昌县|^武功县|^屏边苗族自治县|^建水县|^石屏县|^泸西县|^金平苗族瑶族傣族自治县|^勐海县|^祥云县|^宾川县|^剑川县|^鹤庆县|^绿春县|^河口瑶族自治县|^漾濞彝族自治县|^元阳县|^永平县|^蒙自县|^勐腊县|^弥渡县|^云龙县|^洱源县|^巍山彝族回族自治县|^泸水县|^德钦县|^玛多县|^梁河县|^福贡县|^盈江县|^贡山独龙族怒族自治县|^香格里拉县|^维西傈僳族自治县|^陇川县|^台江县|^纳雍县|^达日县|^弋阳县|^芦山县|^平利县|^班玛县|^榕江县|^锦屏县|^察雅县|^德化县|^夹江县|^荣昌县|^玉田县|^梁平县|^南和县|^新晃侗族自治县|^尉氏县|^应县|^永兴县|^景泰县|^神池县|^丹凤县|^道县|^汉阴县|^左贡县|^鹿寨县|^监利县|^翼城县|^雷波县|^康乐县|^濮阳县|^东兰县|^阳曲县|^乡城县|^崇阳县|^忻城县|^滦平县|^清苑县|^宝应县|^阿克塞哈萨克族自治县|^邵阳县|^文水县|^宜丰县|^永丰县|^无极县|^中方县|^桃源县|^祁连县|^深泽县|^舟曲县|^翁源县|^定结县|^壶关县|^电白县|^大同县|^阿坝县|^浚县|^德清县|^合阳县|^甘泉县|^获嘉县|^措美县|^沙县|^五峰土家族自治县|^吴堡县|^湘潭县|^哈巴河县|^广灵县|^凤冈县|^大田县|^武乡县|^呼玛县|^荥经县|^罗山县|^修文县|^南靖县|^潼关县|^平安县|^贵南县|^开江县|^江安县|^佛冈县|^泾川县|^册亨县|^石阡县|^伊吾县|^子长县|^郎溪县|^沭阳县|^泗洪县|^信丰县|^大厂回族自治县|^临桂县|^汪清县|^酉阳土家族苗族自治县|^盘县|^米易县|^梁山县|^正宁县|^同仁县|^天峨县|^甘洛县|^西华县|^墨玉县|^隆子县|^齐河县|^偏关县|^兴县|^潮安县|^定远县|^绍兴县|^隆回县|^正定县|^尼木县|^延寿县|^临城县|^新宾满族自治县|^襄汾县|^吴桥县|^都昌县|^饶河县|^罗源县|^渑池县|^嵩县|^宽城满族自治县|^沅陵县|^兰西县|^荣县|^安吉县|^曲周县|^镇原县|^上犹县|^平舆县|^彭山县|^阳山县|^中宁县|^潼南县|^新化县|^乌恰县|^洛隆县|^衡东县|^昭平县|^红河县|^祁门县|^沿河土家族自治县|^田东县|^伊通满族自治县|^叙永县|^伊宁县|^玛曲县|^凤翔县|^密云县|^璧山县|^亚东县|^洛川县|^金溪县|^定安县|^汤原县|^中江县|^东乡县|^洛浦县|^定襄县|^温县|^裕民县|^新丰县|^大悟县|^洞口县|^汝南县|^杜尔伯特蒙古族自治县|^志丹县|^华安县|^浑源县|^大通回族土族自治县|^安阳县|^新县|^确山县|^渠县|^湘阴县|^西平县|^元氏县|^英吉沙县|^彰武县|^屏南县|^宣恩县|^茂县|^灵璧县|^竹山县|^比如县|^金沙县|^凤台县|^乡宁县|^长汀县|^江华瑶族自治县|^凌云县|^双牌县|^昌黎县|^芦溪县|^望都县|^通道侗族自治县|^五华县|^依安县|^瓮安县|^迁西县|^交口县|^南城县|^象山县|^喀喇沁左翼蒙古族自治县|^陆川县|^普安县|^建湖县|^浮梁县|^通许县|^甘南县|^大方县|^桑植县|^甘谷县|^怀来县|^会宁县|^乐至县|^浮山县|^宁乡县|^古田县|^抚远县|^札达县|^太和县|^陇县|^卢龙县|^化德县|^唐县|^尼勒克县|^和林格尔县|^大化瑶族自治县|^修水县|^会理县|^本溪满族自治县|^枣强县|^石城县|^仪陇县|^鹤峰县|^建宁县|^龙江县|^玉树县|^精河县|^通榆县|^同心县|^彭阳县|^攸县|^西丰县|^炎陵县|^兴业县|^巴楚县|^临漳县|^淳化县|^九寨沟县|^泸定县|^横山县|^全南县|^青田县|^蒙城县|^贡嘎县|^金堂县|^金乡县|^方山县|^汶川县|^方城县|^山丹县|^横县|^响水县|^宜君县|^日土县|^惠民县|^南昌县|^岳普湖县|^莲花县|^内黄县|^当雄县|^青神县|^青阳县|^右玉县|^巴青县|^会昌县|^滦县|^南丰县|^磁县|^嫩江县|^香河县|^开阳县|^临颍县|^神木县|^嘉禾县|^鸡泽县|^前郭尔罗斯蒙古族自治县|^桦南县|^昔阳县|^江永县|^开县|^怀集县|^张家川回族自治县|^通化县|^庆云县|^阿瓦提县|^庆安县|^普定县|^宁晋县|^南木林县|^凤凰县|^南漳县|^乃东县|^江达县|^固安县|^保靖县|^兴和县|^舒城县|^循化撒拉族自治县|^井研县|^垦利县|^江口县|^巨野县|^瓜州县|^双柏县|^江川县|^易门县|^寻甸回族彝族自治县|^腾冲县|^大关县|^富民县|^陆良县|^宜良县|^沾益县|^水富县|^镇雄县|^龙陵县|^呈贡县|^马龙县|^师宗县|^晋宁县|^嵩明县|^禄劝彝族苗族自治县|^罗平县|^富源县|^澄江县|^华宁县|^施甸县|^昌宁县|^鲁甸县|^盐津县|^彝良县|^威信县|^通海县|^新平彝族傣族自治县|^元江哈尼族彝族傣族自治县|^巧家县|^永善县|^禄丰县|^武定县|^墨江哈尼族自治县|^宁洱哈尼族彝族自治县|^镇沅彝族哈尼族拉祜族自治县|^云县|^姚安县|^永仁县|^玉龙纳西族自治县|^镇康县|^牟定县|^南华县|^永胜县|^凤庆县|^元谋县|^景谷傣族彝族自治县|^永德县|^宁蒗彝族自治县|^景东彝族自治县|^孟连傣族拉祜族佤族自治县|^澜沧拉祜族自治县|^西盟佤族自治县|^双江拉祜族佤族布朗族傣族自治县|^华坪县|^沧源佤族自治县|^双湖县|^荔浦县|^申扎县|^三穗县|^宁县|^礼县|^镇远县|^凤阳县|^平远县|^拉孜县|^太谷县|^汝城县|^浦江县|^金川县|^德庆县|^从江县|^朗县|^泌阳县|^黎城县|^永寿县|^望谟县|^石楼县|^久治县|^故城县|^昭苏县|^宜黄县|^石门县|^邹平县|^屯昌县|^和布克赛尔蒙古自治县|^静宁县|^天柱县|^庆元县|^古丈县|^务川仡佬族苗族自治县|^柞水县|^甘德县|^玛沁县|^环江毛南族自治县|^蒲城县|^宾阳县|^三江侗族自治县|^黎平县|^麻江县|^曲麻莱县|^达孜县|^北川羌族自治县|^平原县|^绥德县|^罗江县|^商河县|^兰坪白族普米族自治县|^随县|^陵水黎族自治县|^保亭黎族苗族自治县|^琼中黎族苗族自治县|^乐东黎族自治县|^庐江县|^无为县|^含山县|^和县|^扶风县|^德昌县|^连江县|^涡阳县|^麦盖提县|^聂拉木县|^邢台县|^宝清县|^蒙阴县|^代县|^武鸣县|^高台县|^绥宁县|^长沙县|^阿克陶县|^京山县|^济阳县|^安福县|^绥滨县|^沁源县|^石台县|^围场满族蒙古族自治县|^九江县|^紫金县|^莎车县|^湟源县|^阜平县|^龙胜各族自治县|^高淳县|^子洲县|^南皮县|^庄浪县|^民乐县|^平乐县|^遂川县|^漠河县|^潜山县|^佛坪县|^夏河县|^大足县|^泽库县|^浪卡子县|^闽清县|^海盐县|^马山县|^英山县|^献县|^尚义县|^广河县|^泾阳县|^得荣县|^望江县|^上思县|^靖西县|^海丰县|^上林县|^五寨县|^濉溪县|^岐山县|^闻喜县|^永嘉县|^武川县|^遂昌县|^巴马瑶族自治县|^祁阳县|^且末县|^赤城县|^繁峙县|^临猗县|^五台县|^山阴县|^萝北县|^和平县|^商城县|^隆德县|^灌云县|^商南县|^黄梅县|^盐源县|^文成县|^洪雅县|^大名县|^龙川县|^临沭县|^东平县|^台安县|^绩溪县|^临泽县|^大埔县|^古浪县|^崇义县|^黄陵县|^永新县|^镇平县|^绥棱县|^高唐县|^灵寿县|^保康县|^碌曲县|^泰和县|^平阳县|^林芝县|^涟水县|^左云县|^蔚县|^黄平县|^拜城县|^咸丰县|^肃宁县|^东山县|^剑河县|^兴山县|^曲松县|^尤溪县|^施秉县|^察隅县|^定兴县|^墨脱县|^淮阳县|^黑山县|^德安县|^滑县|^眉县|^那坡县|^宿松县|^安泽县|^淅川县|^柘荣县|^宁远县|^高陵县|^汝阳县|^涉县|^宁海县|^夏邑县|^绛县|^焉耆回族自治县|^麻栗坡县|^广南县|^西畴县|^南涧彝族自治县|^富宁县|^弥勒县|^砚山县|^马关县|^文山县|^丘北县|^会东县|^独山县|^铜鼓县|^安图县|^岷县|^印江土家族苗族自治县|^雷山县|^宁强县|^文安县|^颍上县|^桦川县|^范县|^丹寨县|^吉木乃县|^延津县|^蕲春县|^平邑县|^平江县|^麻阳苗族自治县|^任县|^互助土族自治县|^潢川县|^米林县|^波密县|^岑巩县|^珙县|^连南瑶族自治县|^巴彦县|^静乐县|^道孚县|^鄄城县|^吴起县|^石林彝族自治县|^会泽县|^绥江县|^峨山彝族自治县|^大姚县|^耿马傣族佤族自治县|^江城哈尼族彝族自治县|^工布江达县|^永福县|^祁东县|^安远县|^东乡族自治县|^丰宁满族自治县|^于田县|^叶城县|^微山县|^改则县";

    /**
     * 区县其他结尾
     */
    private static final String COUNTY_NAME_OTHER_REGEX = "^共青城|^冷湖|^大柴旦|^鄂托克旗|^达拉特旗|^四子王旗|^阿拉善左旗|^阿拉善右旗|^乌拉特前旗|^鄂托克前旗|^西沙群岛|^科尔沁左翼中旗|^察哈尔右翼中旗|^陈巴尔虎旗|^准格尔旗|^太仆寺旗|^额济纳旗|^东海岛|^正镶白旗|^乌拉特中旗|^新巴尔虎右旗|^东乌珠穆沁旗|^莫力达瓦达斡尔族自治旗|^察哈尔右翼后旗|^杭锦旗|^奈曼旗|^巴林左旗|^北屯镇|^阿荣旗|^苏尼特左旗|^中沙群岛的岛礁及其海域|^鄂伦春自治旗|^镶黄旗|^科尔沁右翼前旗|^南沙群岛|^扎赉特旗|^达尔罕茂明安联合旗|^鄂温克族自治旗|^正蓝旗|^全国中心|^巴林右旗|^乌拉特后旗|^阿鲁科尔沁旗|^库伦旗|^土默特右旗|^阿巴嘎旗|^伊金霍洛旗|^科尔沁左翼后旗|^敖汉旗|^喀喇沁旗|^土默特左旗|^科尔沁右翼中旗|^茫崖|^杭锦后旗|^乌审旗|^西乌珠穆沁旗|^苏尼特右旗|^克什克腾旗|^察哈尔右翼前旗|^翁牛特旗|^新巴尔虎左旗|^扎鲁特旗";

    /**
     * 区县名称正则表达式
     */
    private static final String COUNTY_NAME_REGEX = String.format("(%s|%s|%s|%s)(.*)", COUNTY_NAME_CITY_REGEX, COUNTY_NAME_COUNTY_REGEX, COUNTY_NAME_DISTRICT_REGEX, COUNTY_NAME_OTHER_REGEX);

    /**
     * 区县名称正则匹配器
     */
    private static final Pattern COUNTY_NAME_PATTERN = Pattern.compile(COUNTY_NAME_REGEX);


    /**
     * 解析地址
     * @param address
     * @return
     */
    public AddressDetailDto parseAddress(String address) {
        // 去除空格
        address = address.replace(" ", StringUtils.EMPTY);
        List<String> splits = AddressAnalyzer.analyze(address);
        if (CollectionUtils.isEmpty(splits)) {
            throw new RuntimeException("解析地址失败");
        }
        AddressDetailDto result = new AddressDetailDto();
        int endIndex = -1;
        // 解析省份地址
        parseProvince(result, splits.get(0));
        // 省份地址解析失败
        if (StringUtils.isBlank(result.getProvinceName())) {
            // 第一位可能是市开头
            parseCity(result, splits.get(0));
            // 市解析失败，第一位可能是区开头
            if (StringUtils.isBlank(result.getCityName())) {
                parseCounty(result, splits.get(0));
                // 区解析成功
                if (StringUtils.isNotBlank(result.getCountyName())) {
                    endIndex = 0;
                }
            } else {
                // 解析区
                parseCounty(result, splits.get(1));
                if (StringUtils.isNotBlank(result.getCountyName())) {
                    // 区解析成功
                    endIndex = 1;
                }
            }
        } else {
            parseCity(result, splits.get(1));
            // 市解析失败，第二位可能是区开头
            if (StringUtils.isBlank(result.getCityName())) {
                parseCounty(result, splits.get(1));
                // 区解析成功
                if (StringUtils.isNotBlank(result.getCountyName())) {
                    endIndex = 1;
                }
            } else {
                // 解析区县
                // 如果是直辖市,需要特殊处理
                Matcher matcher = MUNICIPALITY_NAME_PATTERN.matcher(result.getProvinceName());
                if (matcher.find()) {
                    parseCounty(result, splits.get(1));
                    if (StringUtils.isNotBlank(result.getCountyName())) {
                        endIndex = 1;
                    } else {
                        // 有直辖市重复情况,再解析下一位
                        parseCounty(result, splits.get(2));
                        if (StringUtils.isNotBlank(result.getCountyName())) {
                            endIndex = 2;
                        } else {
                            log.error("解析区县失败: {}, 地址: {}, 分词: {}", splits.get(1), address, splits.toString());
                        }
                    }
                } else {
                    parseCounty(result, splits.get(2));
                    if (StringUtils.isNotBlank(result.getCountyName())) {
                        endIndex = 2;
                    } else {
                        log.error("解析区县失败: {}, 地址: {}, 分词: {}", splits.get(2), address, splits.toString());
                    }
                }
            }
        }
        // 地址解析成功,截取详情地址
        if (endIndex > -1) {
            result.setDetail(address.substring(address.indexOf(result.getRegex()) + result.getRegex().length()));
        } else {
            // 解析地址失败,有可能存在省市
            if (StringUtils.isNotBlank(result.getProvinceName()) && StringUtils.isNotBlank(result.getCityName())) {
                result.setDetail(address.substring(address.indexOf(result.getRegex()) + result.getRegex().length()));
                return result;
            }
            log.error("解析地址失败: {}, 分词: {}", address, splits.toString());
            return null;
        }
        return result;
    }


    /**
     * 解析快递地址，包含姓名，电话，地址
     * @param address
     * @return
     */
    public ExpressAddressDto parseExpressAddress(String address) {
        ExpressAddressDto dto = split(address);
        if (StringUtils.isEmpty(dto.getAddress())) {
            return dto;
        }
        AddressDetailDto addressDetailDto = parseAddress(dto.getAddress());
        BeanUtil.copyProperties(addressDetailDto, dto);
        return dto;
    }

    /**
     * 批量解析
     * @param addresses
     * @return
     */
    public List<AddressDetailDto> parseAddress(List<String> addresses) {
        List<AddressDetailDto> list = new ArrayList<>(addresses.size());
        for (String address : addresses) {
            list.add(parseAddress(address));
        }
        return list;
    }

    /**
     * 分隔出姓名，电话，地址
     *
     * @param address
     * @return
     */
    public ExpressAddressDto split(String address) {
        ExpressAddressDto dto = new ExpressAddressDto();
        List<String> splits = AddressAnalyzer.unlimitedAnalyze(address);

        if (CollectionUtils.isEmpty(splits)) {
            return dto;
        }

        // 对连续数字分词进行合并
        List<String> reorganization = new ArrayList<>(splits.size());
        boolean number = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < splits.size(); i++) {
            if (NumberUtil.isNumber(splits.get(i))) {
                sb.append(splits.get(i));
                number = true;
            } else {
                if (number) {
                    if (StringUtils.isNotBlank(sb.toString())) {
                        reorganization.add(sb.toString());
                        sb = new StringBuilder();
                    }
                }
                reorganization.add(splits.get(i));
                number = false;
            }
            // 最后一条
            if (i == splits.size() - 1) {
                if (StringUtils.isNotBlank(sb.toString())) {
                    reorganization.add(sb.toString());
                }
            }
        }
        splits = reorganization;

        int phoneIndex = -1;
        for (int i = 0; i < splits.size(); i++) {
            if (RegExUtil.isPhone(splits.get(i))) {
                phoneIndex = i;
                break;
            }
        }
        if (phoneIndex > -1) {
            dto.setPhone(splits.get(phoneIndex));
        }
        // 手机号码的可能性，不存在，在最左侧，在最右侧，在中间
        // 不存在
        if (phoneIndex == -1) {
            splitAddressName(splits, dto);
        } else if (phoneIndex == 0) {
            // 手机号在最左侧
            splitAddressName(splits.subList(1, splits.size()), dto);
        } else if (phoneIndex == splits.size() - 1) {
            // 手机号在最右侧
            splitAddressName(splits.subList(0, phoneIndex), dto);
        } else {
            // 手机号在中间
            // 通常地址在手机号右边，因为先判断右侧是否是地址
            boolean isAddress = false;
            List<String> addressList = splits.subList(phoneIndex + 1, splits.size());
            for (String s : addressList) {
                if (s.length() > 1 && isAddress(s)) {
                    isAddress = true;
                    break;
                }
            }
            if (isAddress) {
                dto.setAddress(composePhrase(splits.subList(phoneIndex + 1, splits.size())));
                if (phoneIndex > 1) {
                    dto.setName(composePhrase(splits.subList(0, phoneIndex)));
                } else {
                    dto.setName(splits.get(0));
                }
            } else {
                dto.setAddress(composePhrase(splits.subList(0, phoneIndex)));
                if (splits.size() - phoneIndex > 2) {
                    dto.setName(composePhrase(splits.subList(phoneIndex + 1, splits.size())));
                } else {
                    dto.setName(splits.get(splits.size() - 1));
                }
            }
        }
        return dto;
    }

    /**
     * 分隔地址和姓名
     *
     * @param splits
     * @param dto
     */
    public void splitAddressName(List<String> splits, ExpressAddressDto dto) {
        int addressIndex = -1;
        int nameIndex = -1;
        for (int i = 0; i < splits.size(); i++) {
            String distName = splits.get(i);
            if (distName.length() > 1) {
                if (isAddress(distName)) {
                    addressIndex = i;
                    break;
                }
            }
        }
        // 地址在后
        if (addressIndex > 0) {
            if (addressIndex > 1) {
                dto.setName(composePhrase(splits.subList(0, addressIndex)));
            } else {
                dto.setName(splits.get(0));
            }
            dto.setAddress(composePhrase(splits.subList(addressIndex, splits.size())));
        }
        // 地址在前
        if (addressIndex == 0) {
            // 判断名字,获取名字的开始下标
            // 从数组尾部开始遍历，判断是否存在名字的姓氏关键字
            boolean surname = false;
            for (int i = splits.size() - 1; i >= 0; i--) {
                if (SurnameUtil.isSurname(splits.get(i))) {
                    surname = true;
                    continue;
                }
                // 排查两个姓氏在一起的情况
                if (surname) {
                    if (SurnameUtil.isSurname(splits.get(i))) {
                        nameIndex = i;
                    } else {
                        nameIndex = i + 1;
                    }
                    break;
                }
                // 最多遍历5次
                if (splits.size() - 5 == i) {
                    break;
                }
            }

            // 如果没有查询到姓氏，则以数组最后的值作为姓名
            if (!surname) {
                if (splits.get(splits.size() - 1).length() > 1) {
                    nameIndex = splits.size() - 1;
                } else {
                    nameIndex = splits.size() - 2;
                }
            }

            // 获取名字
            if (nameIndex == splits.size() - 1) {
                dto.setName(splits.get(nameIndex));
            } else {
                dto.setName(composePhrase(splits.subList(nameIndex, splits.size())));
            }
            dto.setAddress(composePhrase(splits.subList(0, nameIndex)));
        }
    }

    /**
     * List组合成String
     *
     * @param splits
     * @return
     */
    private String composePhrase(List<String> splits) {
        StringBuilder sb = new StringBuilder();
        for (String split : splits) {
            sb.append(split);
        }
        return sb.toString();
    }

    /**
     * 判断是否是地址
     *
     * @param address
     * @return
     */
    private boolean isAddress(String address) {
        if (countAddress(address) > 0) {
            return true;
        }
        return false;
    }

    /**
     * 根据名称查询区域地址数量
     *
     * @param name
     * @return
     */
    private int countAddress(String name) {
        AddressAnalyzeParams params = new AddressAnalyzeParams();
        params.setName(name);
        List<Address> address = addressDataSource.findByNameLike(params);
        if (CollectionUtils.isEmpty(address)) {
            return 0;
        }
        return address.size();
    }

    /**
     * 解析标准地址 -> 省
     *
     * @param result
     * @param province
     * @return
     */
    private AddressDetailDto parseProvince(AddressDetailDto result, String province) {
        if (province.length() < 2) {
            return result;
        }
        AddressAnalyzeParams params = new AddressAnalyzeParams();
        params.setName(province);
        params.setLevel("1");
        List<Address> provinces = addressDataSource.findByNameLike(params);
        if (CollectionUtils.isEmpty(provinces) || provinces.size() > 1) {
            return result;
        }
        Address address = provinces.get(0);
        result.setProvinceName(address.getName());
        result.setProvinceNo(address.getCode());
        result.setRegex(province);
        return result;
    }


    /**
     * 解析标准地址 -> 市
     *
     * @param city
     * @return
     */
    private AddressDetailDto parseCity(AddressDetailDto result, String city) {
        if (city.length() < 2) {
            return result;
        }
        AddressAnalyzeParams params = new AddressAnalyzeParams();;
        params.setName(city);
        params.setLevel("2");
        List<Address> cities = addressDataSource.findByNameLike(params);
        if (CollectionUtils.isEmpty(cities) || cities.size() > 1) {
            // 判断是不是特殊地址
            if (StringUtils.isNotBlank(result.getProvinceName())) {
                Matcher matcher = MUNICIPALITY_NAME_PATTERN.matcher(result.getProvinceName());
                boolean municipality = matcher.find();
                // 如果是直辖市,市与省份一样
                if (municipality) {
                    result.setCityName(result.getProvinceName());
                    result.setCityNo(result.getProvinceNo());
                }
            }
            return result;
        }
        Address address = cities.get(0);
        // 如果省份为空,把省份赋值
        if (StringUtils.isBlank(result.getProvinceName())) {
            result.setProvinceName(address.getProvinceName());
            result.setProvinceNo(address.getProvinceCode());
        }
        // 判断省份与市是否相匹配
        if (address.getProvinceName().equals(result.getProvinceName())) {
            result.setCityName(address.getName());
            result.setCityNo(address.getCode());
            result.setRegex(city);
        }
        return result;
    }

    /**
     * 解析标准地址 -> 区县
     * 地级市 没有区县
     *
     * @param county
     * @return
     */
    private AddressDetailDto parseCounty(AddressDetailDto result, String county) {
        if (county.length() < 2) {
            return result;
        }
        AddressAnalyzeParams params = new AddressAnalyzeParams();;
        params.setName(county);
        params.setLevel("3");
        List<Address> counties = addressDataSource.findByNameLike(params);
        Address address = null;
        if (CollectionUtils.isEmpty(counties)) {
            return result;
        } else if (counties.size() > 1) {
            // 匹配区，出现重名的情况，与市相对比
            for (Address c : counties) {
                if (StringUtils.isNotBlank(c.getCityName())) {
                    if (c.getCityName().equals(result.getCityName())) {
                        address = c;
                        break;
                    }
                }
            }

            if (StringUtils.isNotBlank(result.getProvinceName())) {
                // 如果是直辖市需与省比
                Matcher matcher = MUNICIPALITY_NAME_PATTERN.matcher(result.getProvinceName());
                if (matcher.find()) {
                    for (Address c : counties) {
                        if (c.getProvinceName().equals(result.getProvinceName())) {
                            address = c;
                            break;
                        }
                    }
                }
            }

            if (address == null) {
                return result;
            }

        } else {
            address = counties.get(0);
        }
//        result.setCountyName(address.getName());
//        result.setCountyNo(address.getCode());
        // 如果省市位空,把省市赋值
        if (StringUtils.isBlank(result.getProvinceName())) {
            result.setProvinceName(address.getProvinceName());
            result.setProvinceNo(address.getProvinceCode());
        }
        if (StringUtils.isBlank(result.getCityName())) {
            // 县级市 或直辖市
            if (StringUtils.isBlank(address.getCityName())) {
                Matcher matcher = MUNICIPALITY_NAME_PATTERN.matcher(result.getProvinceName());
                // 直辖市
                if (matcher.find()) {
                    result.setCityName(address.getProvinceName());
                    result.setCityNo(address.getProvinceCode());
                } else {
                    // 县级市
                    result.setCityName(address.getName());
                    result.setCityNo(address.getCode());
                }
            } else {
                result.setCityName(address.getCityName());
                result.setCityNo(address.getCityCode());
            }
        }
        if (StringUtils.isNotBlank(result.getCityName())) {
            Matcher matcher = MUNICIPALITY_NAME_PATTERN.matcher(result.getProvinceName());
            if (matcher.find()) {
                // 直辖市区与省相比(处理市与区不匹配情况)
                if (result.getProvinceName().equals(address.getProvinceName())){
                    result.setCountyName(address.getName());
                    result.setCountyNo(address.getCode());
                } else {
                    return result;
                }
            } else {
                // 区与市相比(处理市与区不匹配情况)
                if (result.getCityName().equals(address.getCityName())){
                    result.setCountyName(address.getName());
                    result.setCountyNo(address.getCode());
                } else {
                    return result;
                }
            }
        }
        result.setRegex(county);
        return result;
    }
}
