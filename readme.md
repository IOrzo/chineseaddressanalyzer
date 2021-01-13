### 自制地址解析

#### 简介

本项目是基于Word分词插件实现的地址解析功能, 解析出地址的省市区和详细地址. 适用于对地址解析要求不高的业务场景. <br />
地址数据是基于2020全国行政区划, 地址是前缀模糊匹配, 可以缺少省市, 如果区是全国唯一, 也能匹配出省和市. <br />

#### 示例数据

**1.地址解析**
四川成都双流高新区姐儿堰路远大都市风景二期
AddressDetailDto(provinceName=四川省, provinceNo=510000, cityName=成都市, cityNo=510100, countyName=双流区, countyNo=510116, detail=高新区姐儿堰路远大都市风景二期, regex=双流)

新疆克拉玛依市拉玛依区油建南路雅典娜74-76
AddressDetailDto(provinceName=新疆维吾尔自治区, provinceNo=650000, cityName=克拉玛依市, cityNo=650200, countyName=null, countyNo=null, detail=拉玛依区油建南路雅典娜74-76, regex=克拉玛依市)

江西省萍乡市经济开发区西区工业园（硖石）金丰路23号江西省萍乡联友建材有限公司
AddressDetailDto(provinceName=江西省, provinceNo=360000, cityName=萍乡市, cityNo=360300, countyName=null, countyNo=null, detail=经济开发区西区工业园（硖石）金丰路23号江西省萍乡联友建材有限公司, regex=萍乡市)

甘肃省嘉峪关前进路车务段9号
AddressDetailDto(provinceName=甘肃省, provinceNo=620000, cityName=嘉峪关市, cityNo=620200, countyName=null, countyNo=null, detail=前进路车务段9号, regex=嘉峪关)

四川省新津县五津镇武阳中路167号2栋1单元6楼2号
AddressDetailDto(provinceName=四川省, provinceNo=510000, cityName=成都市, cityNo=510132, countyName=新津县, countyNo=510132, detail=五津镇武阳中路167号2栋1单元6楼2号, regex=新津县)


双流县海棠湾2栋2单元1102号
AddressDetailDto(provinceName=四川省, provinceNo=510000, cityName=成都市, cityNo=510116, countyName=双流区, countyNo=510116, detail=海棠湾2栋2单元1102号, regex=双流县)


成都市双流区航空港康桥品上2栋1单元1704室
AddressDetailDto(provinceName=四川省, provinceNo=510000, cityName=成都市, cityNo=510100, countyName=双流区, countyNo=510116, detail=航空港康桥品上2栋1单元1704室, regex=双流区)


重庆市江北区南桥寺光华南桥人家2-3-1-1
AddressDetailDto(provinceName=重庆市, provinceNo=500000, cityName=重庆市, cityNo=500000, countyName=江北区, countyNo=500105, detail=南桥寺光华南桥人家2-3-1-1, regex=江北区)

**2.解析带有姓名电话的地址**
最好的解析结果是电话在中间,若姓名地址在一起, 并且地址在前姓名在后, 则无法正确解析出姓名 <br />
WordAddressAnalyzerTest.parseExpressAddress()测试方法

四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装028-88888888张三呀呀
ExpressAddressDto(name=张三呀呀, phone=02888888888, address=四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装)

四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装(028)88888888张三呀呀
ExpressAddressDto(name=张三呀呀, phone=02888888888, address=四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装)

张三呀呀13000000001四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装
ExpressAddressDto(name=张三呀呀, phone=13000000001, address=四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装)

张三呀呀四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装13000000001
ExpressAddressDto(name=张三呀呀, phone=13000000001, address=四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装)

13000000001张三呀呀四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装
ExpressAddressDto(name=张三呀呀, phone=13000000001, address=四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装)

13000000001四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装张三呀呀
ExpressAddressDto(name=呀呀, phone=13000000001, address=四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装张三)

四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装张三呀呀13000000001
ExpressAddressDto(name=呀呀, phone=13000000001, address=四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装张三)

四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装13000000001张三呀呀
ExpressAddressDto(name=张三呀呀, phone=13000000001, address=四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装)

四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装张三呀呀13000000001
ExpressAddressDto(name=呀呀, phone=13000000001, address=四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装张三)


#### 启动项目

**1.导入初始化数据**
将sql文件夹下的address.sql导入到数据库中

**2.修改配置文件**
修改application-dev.yml配置文件, 将数据库信息改为自己的

**3.启动项目**
启动项目主程序ChineseaddressanalyzerApplication, 默认端口为18000

**4.访问测试接口**
解析地址:
http://127.0.0.1:18000/address/analyzer/simple?address=四川成都双流高新区姐儿堰路远大都市风景二期

解析快递地址，包含姓名，电话，地址:
http://127.0.0.1:18000/address/analyzer/expressAddress?address=张三呀呀13000000001四川省乐山市峨眉山市佛欣路19号蒙太奇硅藻泥艺术涂装

**其他**
也可以在测试包路径下直接运行测试示例AddressAnalyzerTest#multiAnalyze()方法查看测试数据

#### 说明

具体实现逻辑在com.sixtofly.chineseaddressanalyzer.service.AddressAnalyzeService类中parseAddress()方法与parseExpressAddress()中, 为了实现功能而逐渐添加判断, 代码写的不好...只是刚刚实现功能。<br />

com.sixtofly.chineseaddressanalyzer.init.LuceneDatasourceInitializer在程序启动完成后会读取MySQL中数据, 建立Lucene文件数据索引, 加快查询速度, 比直接调用MySQL查询效率高。

有些地址信息有变更, 可以修改数据库中address表里数据。有些地址信息有别名, 可以再添加一条数据, 也可以修改alias字段信息。
如: 双流区以前叫双流县, 则alias字段可以赋值为双流县, 地址解析时就可识别





### 参考资料
[Java分布式中文分词组件 - word分词](https://github.com/ysc/word)