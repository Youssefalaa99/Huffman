import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;


public class Main {
    public static void main(String[] args) throws Exception{
        Scanner s = new Scanner(System.in);
        System.out.print("Enter file name: ");
        String fileName = s.nextLine();
        File file = new File(fileName);
        int choice;
        System.out.println("Select: \n1- Compress\n2- Decompress");
        do {
            choice = s.nextInt();
            if(choice == 1){
                long startTime = System.nanoTime();
                double sizeBeforeCompress = file.length();
                compress(file);
                System.out.println("Compression ratio: "+file.length()/sizeBeforeCompress);
                long endTime = System.nanoTime();
                System.out.println("----------------COMPRESSION COMPLETE!----------------");
                System.out.println("Execution time: "+(endTime - startTime)/1000000+"ms");

            }
            else if(choice == 2){
                long startTime = System.nanoTime();
                decompress(file);
                long endTime = System.nanoTime();
                System.out.println("----------------DECOMPRESSION COMPLETE!----------------");
                System.out.println("Execution time: "+(endTime - startTime)/1000000+"ms");
            }
            else{
                System.out.println("Please select 1 or 2...");
            }
        }while (choice != 1 && choice != 2);
    }

    private static void compress(File file) throws Exception{
        //Reading file and constructing a frequency hash-map for each character
        BufferedReader br = new BufferedReader(new FileReader(file));
        HashMap<String,Integer> hashMap = new HashMap<>();   //Frequency hash-map
        int r;
        while ((r = br.read()) != -1) {
            char ch = (char) r;
            String myStr = Character.toString(ch);
            if(myStr.equals("\r")){
                continue;
            }
            if(!hashMap.containsKey(myStr)){
                hashMap.put(myStr,1);
            }
            else {
                Integer val = new Integer(hashMap.get(myStr));
                val++;
                hashMap.replace(myStr,val);
            }
        }
        br.close();


        PriorityQueue<HuffmanNode> pq
                = new PriorityQueue<>(hashMap.size(), new NodeComparator());

        //Building min heap using priority queue
        for (HashMap.Entry<String, Integer> entry : hashMap.entrySet()) {
            String c = entry.getKey();
            Integer freq = entry.getValue();
            HuffmanNode node = new HuffmanNode(freq,c);
            pq.add(node);
        }

        //Create Huffman tree
        HuffmanTree tree = new HuffmanTree(pq);
        tree.createHuffmanTree();
        //Setting Huffman code for each character
        tree.setCode(tree.getRoot(),"");
        //Print Huffman codes
        tree.printCodeMap();


        //Reading file once again to encode it to bits
        br = new BufferedReader(new FileReader(file));
        StringBuilder compressedCodeBuilder = new StringBuilder();
        while ((r = br.read()) != -1) {
            char ch = (char) r;
            String myStr = Character.toString(ch);
            if(myStr.equals("\r")){
                continue;
            }
            String code = tree.getCode(myStr);
            compressedCodeBuilder.append(code);   //Append to final encoded string

        }
        br.close();
        String compressedCode = compressedCodeBuilder.toString();

        //Splitting final encoded bits to 8 bit strings
        String[] subStrings = splitToNChar(compressedCode, 8);


        FileWriter fw = new FileWriter(file);

        //Adding huffman codes to header of compressed file
        for (HashMap.Entry<String, String> entry : tree.getCodeMap().entrySet()) {
            String s = entry.getKey();
            String code = entry.getValue();
            fw.write(s + code + ",");
        }
        //Adding size of last sub strings to header to remove padding in decompression
        int sizeOfLastSubString = subStrings[subStrings.length-1].length();
        fw.write(sizeOfLastSubString+",");  //Size of last substring
        fw.write(" ,");     //Termination of header delimiter

        //Encoding each 8 bit string to characters and writing them to file
        for(int i=0; i<subStrings.length; i++){
            Integer num = Integer.parseInt(subStrings[i],2);  //Decimal form of selected byte
            char c = (char)num.intValue();   //Character representation of selected byte
            fw.write(c);
        }
        fw.close();

    }


    private static void decompress(File file) throws Exception{
        //Initialization of size of last 8 bits
        int sizeOfLastSubString = 0;

        //Reading compressed file
        //Reading header
        HashMap<String, String> codeMap = new HashMap<>();  //Map of huffman codes

        Scanner read = new Scanner(file);
        read.useDelimiter(",");
        while (read.hasNext()) {
            String myStr = read.next();
            if(myStr.length() == 1 && myStr.equals(" ")){  //Done reading code map, end reading to start reading encoded characters
                break;
            }
            else if(myStr.length() == 1){           //Read size of last 8 bits
                sizeOfLastSubString = Integer.parseInt(myStr);
                continue;
            }
            if(myStr.length() == 0){  //Reading ","
                String val = ",";
                String key = read.next();
                codeMap.put(key,val);
                continue;
            }

            String val = myStr.substring(0,1);
            String key = myStr.substring(1);
            codeMap.put(key,val);
        }
        read.reset();  //Remove "," delimiter

        //Printing code map
        for (HashMap.Entry<String, String> entry : codeMap.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }

        //Reading compressed content and decoding them to bits
        read.useDelimiter("(?<=.)");  //Scanner reads character by character
        read.next();
        StringBuilder codeBuilder = new StringBuilder();
        while (read.hasNext()){
            String character = read.next();
            String bits = convertToBinary(character.charAt(0));
            if(character.length() >1){   //Read new line and character
                codeBuilder.append(bits);       //Add new line bits
                bits = convertToBinary(character.charAt(1));
                codeBuilder.append(bits);       //Add character bits
                continue;
            }
            codeBuilder.append(bits);
        }
        read.close();
        String code = codeBuilder.toString();
        //Remove padding from last 8 bit string
        String lastSubString = code.substring(code.length()-8);
        lastSubString = lastSubString.substring(8-sizeOfLastSubString);
        code = code.substring(0,code.length()-8) + lastSubString;


        //Decoding
        FileWriter fw = new FileWriter(file);
        String currentCode = "";
        for(int i = 0; i<code.length(); i++){
            currentCode = currentCode + code.charAt(i);
            if(codeMap.containsKey(currentCode)){
                fw.write(codeMap.get(currentCode));
                currentCode = "";
            }
        }
        fw.close();

    }

    private static String[] splitToNChar(String text, int N) {   //Function returns string to array of N strings
        List<String> parts = new ArrayList<>();

        int length = text.length();
        for (int i = 0; i < length; i += N) {
            parts.add(text.substring(i, Math.min(length, i + N)));
        }
        return parts.toArray(new String[0]);
    }

    private static String convertToBinary(char c){              //Function returns bit format of character
        int decimalRep = (int)c;        //Get decimal representation
        String bits = Integer.toBinaryString(decimalRep);   //Convert to bits
        while (bits.length() < 8){   //Padding
            bits = "0" + bits;
        }
        return bits;
    }
}
