package src;

class ColumnData {
    private int col_timestamp;
    private int col_position_lat;
    private int col_position_lon;
    private int col_distance;
    private int col_altitude;
    private int col_speed;
    private int col_temprature;
    final String UNIT_SEMI = "semicircles";
    final String UNIT_DEG = "degrees";
    private int lineSize; // 1行の大きさ

    public int getCol_timestamp() {return col_timestamp;}
    public int getCol_position_lat() {return col_position_lat;}
    public int getCol_position_lon() {return col_position_lon;}
    public int getCol_distance() {return col_distance;}
    public int getCol_altitude() {return col_altitude;}
    public int getLineSize() {return lineSize;}

    ColumnData(){
        col_timestamp = 4;
        col_position_lat = 7;
        col_position_lon = 10;
        col_distance = 13;
        col_altitude = 16;
        col_speed = 19;
        col_temprature = 22;
        lineSize = 248;
    }

    void columnDataSetter(String[] data){
        lineSize = 0;
        int i = 0;
        for (String string : data) {
            lineSize += string.length();
            lineSize++; // コンマの分

            switch (string) {
                //項目名が出てきたら、次の列がデータ入っているところ
                case "timestamp": col_timestamp = i+1; break;
                case "position_lat": col_position_lat = i+1; break;
                case "position_long": col_position_lon = i+1; break;
                case "distance": col_distance = i+1; break;
                case "altitude": col_altitude = i+1; break;
                case "speed": col_speed = i+1; break;
                case "temprature": col_temprature = i+1; break;
                default: break;
            }

            i++;
        }
    }

    void printData(){
        printTimestamp();printPositionLat();printPositionLon();printDistance();
        printAltitude();printSpeed();printTemprature();printLineSize();
    }

    private void printTimestamp(){  System.out.println("timestamp:\t"+col_timestamp);}
    private void printPositionLat(){System.out.println("position_lat:\t"+col_position_lat);}
    private void printPositionLon(){System.out.println("position_lon:\t"+col_position_lon);}
    private void printDistance(){   System.out.println("distance:\t"+col_distance);}
    private void printAltitude(){   System.out.println("altitude:\t"+col_altitude);}
    private void printSpeed(){      System.out.println("speed:\t\t"+col_speed);}
    private void printTemprature(){ System.out.println("temprature\t"+col_temprature);}
    private void printLineSize(){   System.out.println("lineSize\t"+lineSize);}
}