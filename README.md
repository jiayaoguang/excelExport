# excelExport
 excel表格数据导出为json数据，并生成对应代码

Excel 表配置 :

![img.png](img.png)

生成的java代码 :

    public class ConfTest {
    
        private String name;
        private int id;
        private long time;
    
        public ConfTest() {
        }
    
        private String name;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        private int id;
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        private long time;
        public long getTime() {
            return time;
        }
        public void setTime(long time) {
            this.time = time;
        }
    
    
    }