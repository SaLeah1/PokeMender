package junior_is;

public class TypeBot {
    public String[] names;
    public double[][] vals;

    public TypeBot(){
        this.names = new String[]{
            "normal","fire","water","electric","grass","ice","fighting","poison","ground",
            "flying","psychic","bug","rock","ghost","dragon","dark","steel","fairy","" // "" for missing secondary type 
        }; // this was fun to make :^ )
        double[] normOff = new double[]{1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,0.5,0.0,1.0,1.0,0.5,1.0,1.0};
        double[] fireOff = new double[]{1.0,0.5,0.5,1.0,2.0,2.0,1.0,1.0,1.0,1.0,1.0,2.0,0.5,1.0,0.5,1.0,2.0,1.0,1.0};
        double[] watrOff = new double[]{1.0,2.0,0.5,1.0,0.5,1.0,1.0,1.0,2.0,1.0,1.0,1.0,2.0,1.0,0.5,1.0,1.0,1.0,1.0};
        double[] elecOff = new double[]{1.0,1.0,2.0,0.5,0.5,1.0,1.0,1.0,0.0,2.0,1.0,1.0,1.0,1.0,0.5,1.0,1.0,1.0,1.0};
        double[] grasOff = new double[]{1.0,0.5,2.0,1.0,0.5,1.0,1.0,0.5,2.0,0.5,1.0,0.5,2.0,1.0,0.5,1.0,0.5,1.0,1.0};
        double[] iceOff  = new double[]{1.0,0.5,0.5,1.0,2.0,0.5,1.0,1.0,2.0,2.0,1.0,1.0,1.0,1.0,2.0,1.0,0.5,1.0,1.0};
        double[] fghtOff = new double[]{2.0,1.0,1.0,1.0,1.0,2.0,1.0,1.0,1.0,0.5,0.5,0.5,2.0,0.0,1.0,2.0,2.0,0.5,1.0};
        double[] poisOff = new double[]{1.0,1.0,1.0,1.0,2.0,1.0,1.0,0.5,0.5,1.0,1.0,1.0,0.5,0.5,1.0,1.0,0.0,2.0,1.0};
        double[] grndOff = new double[]{1.0,2.0,1.0,2.0,0.5,1.0,1.0,2.0,1.0,0.0,1.0,0.5,2.0,1.0,1.0,1.0,2.0,1.0,1.0};
        double[] flyOff  = new double[]{1.0,1.0,1.0,0.5,2.0,1.0,2.0,1.0,1.0,1.0,1.0,2.0,0.5,1.0,1.0,1.0,0.5,1.0,1.0};
        double[] psyOff  = new double[]{1.0,1.0,1.0,1.0,1.0,1.0,2.0,0.5,1.0,1.0,0.5,1.0,1.0,1.0,1.0,0.0,0.5,1.0,1.0};
        double[] bugOff  = new double[]{1.0,0.5,1.0,1.0,2.0,1.0,0.5,0.5,1.0,0.5,2.0,1.0,1.0,0.5,1.0,2.0,0.5,0.5,1.0};
        double[] rockOff = new double[]{0.0,2.0,1.0,1.0,1.0,2.0,0.5,1.0,0.5,2.0,1.0,2.0,1.0,1.0,1.0,1.0,0.5,1.0,1.0};
        double[] ghstOff = new double[]{0.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,2.0,1.0,1.0,2.0,1.0,0.5,1.0,1.0,1.0};
        double[] drgnOff = new double[]{1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,2.0,1.0,0.5,0.0,1.0};
        double[] darkOff = new double[]{1.0,1.0,1.0,1.0,1.0,1.0,0.5,1.0,1.0,1.0,2.0,1.0,1.0,2.0,1.0,0.5,1.0,0.5,1.0};
        double[] stlOff  = new double[]{1.0,0.5,0.5,0.5,1.0,2.0,1.0,1.0,1.0,1.0,1.0,1.0,2.0,1.0,1.0,1.0,0.5,2.0,1.0};
        double[] fairOff = new double[]{1.0,0.5,1.0,1.0,1.0,1.0,2.0,0.5,1.0,1.0,1.0,1.0,1.0,1.0,2.0,2.0,0.5,1.0,1.0};
        this.vals = new double[][]{normOff,fireOff,watrOff,elecOff,grasOff,iceOff,fghtOff,poisOff,grndOff,flyOff,
            psyOff,bugOff,rockOff,ghstOff,drgnOff,darkOff,stlOff,fairOff};
    }
    public double typeMatch(String offensiveType, String defensiveType){ // because java is too good for default params
        return typeMatch(offensiveType, defensiveType,"");
    }
    public double typeMatch(String offensiveType, String defensiveType1, String defensiveType2){
        int itt = 0;
        int offIndx = 18; // 18 is null input
        int def1Indx = 18;
        int def2Indx = 18;
        while(itt<this.names.length){
            if (this.names[itt].equals(offensiveType)){
                offIndx = itt;
            } else if (this.names[itt].equals(defensiveType1)){
                def1Indx = itt;
            } else if (this.names[itt].equals(defensiveType2)){
                def2Indx = itt;
            } itt++;
        }
        double val = this.vals[offIndx][def1Indx]*this.vals[offIndx][def2Indx];
        if(val==0){val = -3.;}
        else if(val==0.25){val = -2.;}
        else if(val==0.5){val = -1.;}
        else if(val==0.){val = 0.;}
        else if(val==0.){val = 1.;}
        else if(val==0.){val = 2.;}
        return val;
    }
    public static void main(String[] args) {
        TypeBot jimmy = new TypeBot();
        System.out.println(jimmy.typeMatch("normal", "ghost"));
        System.out.println(jimmy.typeMatch("normal", "ghost","dark"));
    }
}
