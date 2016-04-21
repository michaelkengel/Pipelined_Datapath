import java.util.Scanner;
import java.util.Stack;

public class Driver {

	static int[] MainMem = new int[1024];
	static int[] Regs = new int[32];
	public static Stack<Integer> instructionCache = new Stack<Integer>();

	// MAIN METHOD

	public static void main(String[] args) {
		Scanner kb = new Scanner(System.in);

		// INITIALIZE 
		setIns();
		setMem();
		setRegs();

		// ONE CYCLE BLOCK
		int Cycle = 1;
		boolean run = true;
		while(!instructionCache.isEmpty()){
			System.out.println("**** Cycle Number " + Cycle + " ***");
			System.out.println("RUNNING STAGES");
			IF_stage();
			ID_stage();		
			EX_stage();
			MEM_stage(); 		
			WB_stage(); 		
			Print_out_everything(); 
			System.out.println("\nCopying over data....");
			Copy_write_to_read();
			System.out.println("Complete.\n");
			Cycle++;
			System.out.println("Press Enter to cycle");
			kb.nextLine();
		}
		
		kb.close();
		System.out.println("Complete.");
	}

	// METHODS WILL BE CALLED IN ORDER 

	private static void setIns(){
		
		instructionCache.push(0x00000000);
		instructionCache.push(0x00000000);
		instructionCache.push(0x00000000);
		instructionCache.push(0x00000000);
		instructionCache.push(0x00624022);
		instructionCache.push(0x81510010);
		instructionCache.push(0x81180000);
		instructionCache.push(0x01224820);
		instructionCache.push(0x01263820);
		instructionCache.push(0x00831820);
		instructionCache.push(0x810AFFFC);
		instructionCache.push(0xA1020000);
				
	}

	private static void setRegs(){
		for (int i = 0,  index = 256; i < 32; i++, index++) {
			Regs[i]= index;
		}
		Regs[0]= 0;
	}

	private static void showRegs(){
		for (int i = 0; i < 32; i++) {
			System.out.println("Register " + i + " " + Integer.toHexString(Regs[i]));
		}
	}

	private static void setMem(){

		int counter = 0;
		while (counter < 1024){
			for (int i =0; i < 256 ; i++){
				MainMem[counter] = (short) i;
				counter++;
			}
		}
	}

	private static void showMem(){ // For testing
		for (int i = 0; i < 1024; i++) {
			System.out.println("Mein Mem address " + i + " in hex "+Integer.toHexString(i) +" Holds value: "+ MainMem[i]+ " in Hex: " + Integer.toHexString(MainMem[i]));	
		}
	}

	public static void IF_stage(){
		// Read instruction from PC counter, it will increment
		WRITE_IFID.Instruction = instructionCache.pop();
	}

	public static void ID_stage(){
		// Here you'll read an instruction from the READ version of IF/ID pipeline register
		int Instruction = READ_IFID.Instruction;
		//do the decoding 
		
		if (Instruction != 0){

		// Info
		
		int ReadReg1Value=0;
		int ReadReg2Value=0;
		int SEOoffset=0;
		int Write_Reg20_16=0;
		int Write_Reg15_11=0;
		int function=0;
			
		// CONTROL
	
		int RegDest=0; // 0 if 16-20 bits for I-type vs 1 for bits 11-15 for R-Type
		int ALUsrc=0; // false for R type, true for I type
		int ALUop=0;   // 10 if R, 00 if load or store 
		int MemRead=0; // true for I- type or false for R-type
		int MemWrite=0; //
		int RegWrite=0; // 1 for R type or True
		int MemToReg=0; // 0 for R type
		
		String temp = Integer.toBinaryString(Instruction);
		// Make sure binary string is 32 bits, add zeros if needed
		for (int i = temp.length(); i != 32; i++){
			temp = "0" + temp;
		} 

		//and register fetching 
		// SEND to R-Type if beginning with 000000
		if (temp.substring(0,6).contains("000000")){
			
			RegDest = 1;
			ALUsrc = 0;
			ALUop = 1;
			MemRead = 0;
			MemWrite = 0;
			RegWrite = 1;
			MemToReg = 0;
			
			String rs = temp.substring(6,11);
			int rsAddress = Integer.parseInt(rs,2); // GET RS REGISTER NUMBER
			ReadReg1Value = Regs[rsAddress];   // GET VALUE		

			String rt = (temp.substring(11,16));
			Write_Reg20_16 = Integer.parseInt(rt,2); // GET RT REGISTER NUMBER
			ReadReg2Value = Regs[Write_Reg20_16];   // GET VALUE
			

			String rd = (temp.substring(16,21));
			Write_Reg15_11 = Integer.parseInt(rd,2); // GET RD REGISTER NUMBER
			

			String funct = temp.substring(26,32);
			function = Integer.parseInt(funct, 2);
			SEOoffset = 0;

		}
		// SEND TO I TYPE INSTRUCTION
		else {
			
			RegDest = 0;
			ALUsrc = 1;
			ALUop = 0; // Set to 0
			MemRead = 0;
			MemWrite = 0;
			RegWrite = 0;
			MemToReg = 0;

			String offSetasString = null;

			String op = temp.substring(0,6);
			function = Integer.parseInt(op,2);

			String rs = temp.substring(6,11);
			int rsAddress = Integer.parseInt(rs,2); // GET RS REGISTER NUMBER
			ReadReg1Value = Regs[rsAddress];   // GET VALUE	

			String rt = (temp.substring(11,16));
			Write_Reg20_16 = Integer.parseInt(rt,2); // GET RT REGISTER NUMBER
			ReadReg2Value = Regs[Write_Reg20_16];   // GET VALUE
			//Get offset address
			offSetasString = temp.substring(16);
			SEOoffset = (short) Long.parseLong(offSetasString,2); // GET OFFSET

			Write_Reg15_11 = 0;

			if (function == 0x20){ // if it is load BYTE (Change here to 20 for byte)
				RegWrite = 1;
				MemToReg = 1;
				MemWrite = 0;
				MemRead = 1;
			}
			if (function == 0x28){ // if it is STORE BYTE
				MemWrite = 1;
				MemRead = 0;
				MemToReg = 0;
				RegWrite = 0;
			}
		}

		//and write the values to the WRITE version of the ID/EX pipeline register.

		// Control
		
		WRITE_IDEX.RegDest = RegDest;
		WRITE_IDEX.ALUsrc = ALUsrc;
		WRITE_IDEX.ALUop = ALUop;
		WRITE_IDEX.MemRead = MemRead;
		WRITE_IDEX.MemWrite = MemWrite;
		WRITE_IDEX.RegWrite = RegWrite;
		WRITE_IDEX.MemToReg = MemToReg;
		
		// Info
		
		WRITE_IDEX.ReadReg1Value = ReadReg1Value;
		WRITE_IDEX.ReadReg2Value = ReadReg2Value;
		WRITE_IDEX.SEOoffset = SEOoffset;
		WRITE_IDEX.Write_Reg15_11 = Write_Reg15_11;
		WRITE_IDEX.Write_Reg20_16 = Write_Reg20_16;
		WRITE_IDEX.function = function;
		}
		else{
			
			WRITE_IDEX.RegDest = 0;
			WRITE_IDEX.ALUsrc = 0;
			WRITE_IDEX.ALUop = 0;
			WRITE_IDEX.MemRead = 0;
			WRITE_IDEX.MemWrite = 0;
			WRITE_IDEX.RegWrite = 0;
			WRITE_IDEX.MemToReg = 0;
			
			// Info
			
			WRITE_IDEX.ReadReg1Value = 0;
			WRITE_IDEX.ReadReg2Value = 0;
			WRITE_IDEX.SEOoffset = 0;
			WRITE_IDEX.Write_Reg15_11 = 0;
			WRITE_IDEX.Write_Reg20_16 = 0;
			WRITE_IDEX.function = 0;
		}
	}

	private static void EX_stage(){
		// Here you'll perform the requested instruction on the specific operands you read 
		//out of the READ version of the IDEX pipeline register 
		
		// Info

		int ReadReg1Value = READ_IDEX.ReadReg1Value;
		int ReadReg2Value = READ_IDEX.ReadReg2Value;
		int SEOoffset = READ_IDEX.SEOoffset;
		int Write_Reg20_16 = READ_IDEX.Write_Reg20_16;
		int Write_Reg15_11 = READ_IDEX.Write_Reg15_11;
		int function = READ_IDEX.function;

		// CONTROL

		int RegDest = READ_IDEX.RegDest; // false(0) if 16-20 bits for I-type vs treu(1) for bits 11-15 for R-Type
		int ALUop = READ_IDEX.ALUop;   // 10 if R, 00 if load or store 
		int MemRead = READ_IDEX.MemRead; // true for LOAD WORD
		int MemWrite = READ_IDEX.MemWrite; // true for STORE WORD
		int RegWrite = READ_IDEX.RegWrite; // 1 for R type or load word
		int MemToReg = READ_IDEX.MemToReg; // 0 for R type

		// New Instantiation
		
		int ALU_RESULT = 0;
		int WriteRegNum;
		
		if (RegDest == 1){
			WriteRegNum = Write_Reg15_11;
		}
		else 
			WriteRegNum = Write_Reg20_16;
		
		// IF IT IS R-TYPE
		if(ALUop == 1){ // Check ALUop for 10 (using boolean so true)
			switch(function){
			case (0x20):{ // ADD
				// RD = RS + RT
				ALU_RESULT = ReadReg1Value + ReadReg2Value;
				break;
				}
			case (0x22):{ // SUB
				ALU_RESULT = ReadReg1Value - ReadReg2Value;
				break;
				}

			case (0):{ // NOP
				break;	
				}
			}
		}
		else{			
			switch(function){
			case (0x20):{ // LOAD BYTE
				// RT = RS(+Offest)
				ALU_RESULT = ReadReg1Value + SEOoffset;
				break;
				}
			case (0x28):{ // STORE BYTE
				// 
				ALU_RESULT = ReadReg2Value + SEOoffset;
				break;
				}
			case (0):{ // NOP
				break;	
				}
			}			
		}
		// and then write the appropriate values to the WRITE version of the EX/MEM pipeline register.
		
		WRITE_EXMEM.ALUresult = ALU_RESULT;
		WRITE_EXMEM.SWValue = ReadReg1Value;
		WRITE_EXMEM.WriteRegNum = WriteRegNum;

		// CONTROL

		WRITE_EXMEM.MemRead = MemRead;
		WRITE_EXMEM.MemWrite = MemWrite;
		WRITE_EXMEM.MemToReg = MemToReg;
		WRITE_EXMEM.RegWrite = RegWrite;
	}

	private static void MEM_stage(){
		// MEM If the instruction is a lb, then use the address you calculated in the EX stage
		//as an index into your Main Memory array and get the value that is there. 
		// READ VALUES

		int MemRead = READ_EXMEM.MemRead;
		int MemWrite = READ_EXMEM.MemWrite;
		int MemToReg = READ_EXMEM.MemToReg;
		int RegWrite = READ_EXMEM.RegWrite;
		int ALU_Result = READ_EXMEM.ALUresult;
		int SWValue = READ_EXMEM.SWValue;
		int WriteRegNum = READ_EXMEM.WriteRegNum;

		/// New VALUES
		int LWDataValue= 0;

		if (MemRead == 0 && MemWrite == 1 && RegWrite == 0 && MemToReg == 0){
			MainMem[ALU_Result] = SWValue;
		}
		else {
			if (MemWrite == 1){
				MainMem[ALU_Result] = SWValue;
			}
			else if (MemRead == 1){
				LWDataValue = MainMem[ALU_Result];
				System.out.println("Set LWData val as " + LWDataValue);
			}
		}
		// Otherwise, just pass information from the READ version of the EX_MEM pipeline register to the WRITE version of MEM_WB.

		WRITE_MEMWB.LWDataValue = LWDataValue;
		WRITE_MEMWB.ALU_Result = ALU_Result;
		WRITE_MEMWB.MemToReg = MemToReg;
		WRITE_MEMWB.RegWrite = RegWrite;
		WRITE_MEMWB.WriteRegNumber = WriteRegNum;

	}

	private static void WB_stage(){
		// Write to the registers based on information you read out of the READ version of MEM_WB.

		int MemToReg = READ_MEMWB.MemToReg; // lw
		int RegWrite = READ_MEMWB.RegWrite; 
		int LWDataValue = READ_MEMWB.LWDataValue; // lw
		int ALU_Result = READ_MEMWB.ALU_Result;
		int WriteRegNumber = READ_MEMWB.WriteRegNumber;
		
		// ADD, SUB, LOAD
		if (RegWrite == 1){
		
			// LOAD 
			if (MemToReg == 1){
				Regs[WriteRegNumber] = LWDataValue;
			}
			
			// ADD or SUB
			else {
				Regs[WriteRegNumber] = ALU_Result;
			}
		}		
	}

	private static void Print_out_everything(){
		System.out.println("Registers:");
		showRegs();
		System.out.println();
		System.out.println("Pipeline Reg Values\n");
		System.out.println("IFID WRITE\n");
		System.out.println("Instruction: " + Integer.toHexString(WRITE_IFID.Instruction));
		System.out.println("IFID READ");
		System.out.println("Instruction: " + Integer.toHexString(READ_IFID.Instruction));
		System.out.println();
		System.out.println("IDEX WRITE");
		System.out.println("Read reg 1 value: " + Integer.toHexString(WRITE_IDEX.ReadReg1Value));
		System.out.println("Read reg 2 value: " + Integer.toHexString(WRITE_IDEX.ReadReg2Value));
		System.out.println("SEOffset: " + Integer.toHexString(WRITE_IDEX.SEOoffset));
		System.out.println("Write reg 16-20: " + WRITE_IDEX.Write_Reg20_16);
		System.out.println("Write reg 11-15: " + WRITE_IDEX.Write_Reg15_11);
		System.out.println("Function (Hex): " + Integer.toHexString(WRITE_IDEX.function));
		System.out.println("Control");
		System.out.println("RegDest: " + WRITE_IDEX.RegDest );
		System.out.println("ALUop: " + WRITE_IDEX.ALUop);
		System.out.println("ALUsrc: " + WRITE_IDEX.ALUsrc);
		System.out.println("MemRead: " + WRITE_IDEX.MemRead);
		System.out.println("MemWrite: " + WRITE_IDEX.MemWrite);
		System.out.println("RegWrite: " + WRITE_IDEX.RegWrite);
		System.out.println("MemToReg: " + WRITE_IDEX.MemToReg);
		System.out.println();
		System.out.println("IDEX READ");
		System.out.println("Read reg 1 value: " + Integer.toHexString(READ_IDEX.ReadReg1Value));
		System.out.println("Read reg 2 value: " + Integer.toHexString(READ_IDEX.ReadReg2Value));
		System.out.println("SEOffset: " + Integer.toHexString(READ_IDEX.SEOoffset));
		System.out.println("Write reg 16-20: " + READ_IDEX.Write_Reg20_16);
		System.out.println("Write reg 11-15: " + READ_IDEX.Write_Reg15_11);
		System.out.println("Function (Hex): " + Integer.toHexString(READ_IDEX.function));
		System.out.println("Control");
		System.out.println("RegDest: " + READ_IDEX.RegDest );
		System.out.println("ALUop: " + READ_IDEX.ALUop);
		System.out.println("ALUsrc: " + READ_IDEX.ALUsrc);
		System.out.println("MemRead: " + READ_IDEX.MemRead);
		System.out.println("MemWrite: " + READ_IDEX.MemWrite);
		System.out.println("RegWrite: " + READ_IDEX.RegWrite);
		System.out.println("MemToReg: " + READ_IDEX.MemToReg);
		System.out.println();
		System.out.println("EXMEM WRITE");
		System.out.println("ALU Result: " + Integer.toHexString(WRITE_EXMEM.ALUresult));
		System.out.println("SWValuye: " + Integer.toHexString(WRITE_EXMEM.SWValue));
		System.out.println("Write Reg Num: " + WRITE_EXMEM.WriteRegNum);
		System.out.println("Control:");
		System.out.println("MemRead: " + WRITE_EXMEM.MemRead );
		System.out.println("MemWrite: " + WRITE_EXMEM.MemWrite);
		System.out.println("MemToReg: " + WRITE_EXMEM.MemToReg);
		System.out.println("RegWrite: " + WRITE_EXMEM.RegWrite);
		System.out.println();
		System.out.println("EXMEM READ");	
		System.out.println("ALU Result: " + Integer.toHexString(READ_EXMEM.ALUresult));
		System.out.println("SWValuye: " + Integer.toHexString(READ_EXMEM.SWValue));
		System.out.println("Write Reg Num: " + READ_EXMEM.WriteRegNum);
		System.out.println("Control:");
		System.out.println("MemRead: " + READ_EXMEM.MemRead );
		System.out.println("MemWrite: " + READ_EXMEM.MemWrite);
		System.out.println("MemToReg: " + READ_EXMEM.MemToReg);
		System.out.println("RegWrite: " + READ_EXMEM.RegWrite);
		System.out.println();
		System.out.println("MEMWB WRITE");	
		System.out.println("LWDataValue: " + Integer.toHexString(WRITE_MEMWB.LWDataValue));
		System.out.println("ALU RESULT: " + Integer.toHexString(WRITE_MEMWB.ALU_Result));
		System.out.println("WriteRegNumber: " + WRITE_MEMWB.WriteRegNumber);
		System.out.println("Control:");
		System.out.println("MemToReg: " + WRITE_MEMWB.MemToReg);
		System.out.println("RegWrite: " + WRITE_MEMWB.RegWrite);
		System.out.println();
		System.out.println("MEMWB READ");
		System.out.println("LWDataValue: " + Integer.toHexString(READ_MEMWB.LWDataValue));
		System.out.println("ALU RESULT: " + Integer.toHexString(READ_MEMWB.ALU_Result));
		System.out.println("WriteRegNumber: " + READ_MEMWB.WriteRegNumber);
		System.out.println("Control:");
		System.out.println("MemToReg: " + READ_MEMWB.MemToReg);
		System.out.println("RegWrite: " + READ_MEMWB.RegWrite);

	}

	private static void Copy_write_to_read(){
		// 
	// COPY OVER IDEX
		
		READ_IDEX.ReadReg1Value = WRITE_IDEX.ReadReg1Value;
		READ_IDEX.ReadReg2Value = WRITE_IDEX.ReadReg2Value;
		READ_IDEX.SEOoffset = WRITE_IDEX.SEOoffset;
		READ_IDEX.Write_Reg20_16 = WRITE_IDEX.Write_Reg20_16;
		READ_IDEX.Write_Reg15_11 = WRITE_IDEX.Write_Reg15_11;
		READ_IDEX.function = WRITE_IDEX.function;
		READ_IDEX.RegDest = WRITE_IDEX.RegDest; // 0 if 16-20 bits for I-type vs 1 for bits 11-15 for R-Type
		READ_IDEX.ALUsrc = WRITE_IDEX.ALUsrc; // false for R type, true for I type
		READ_IDEX.ALUop = WRITE_IDEX.ALUop;   // 10 if R, 00 if load or store 
		READ_IDEX.MemRead = WRITE_IDEX.MemRead; // true for I- type or false for R-type
		READ_IDEX.MemWrite = WRITE_IDEX.MemWrite; //
		READ_IDEX.RegWrite = WRITE_IDEX.RegWrite; // 1 for R type or True
		READ_IDEX.MemToReg = WRITE_IDEX.MemToReg; // 0 for R type

		// COPY EXMEM

		READ_EXMEM.ALUresult = WRITE_EXMEM.ALUresult;
		READ_EXMEM.SWValue = WRITE_EXMEM.SWValue;
		READ_EXMEM.WriteRegNum = WRITE_EXMEM.WriteRegNum;
		READ_EXMEM.MemRead = WRITE_EXMEM.MemRead;
		READ_EXMEM.MemWrite = WRITE_EXMEM.MemWrite;
		READ_EXMEM.MemToReg = WRITE_EXMEM.MemToReg;
		READ_EXMEM.RegWrite = WRITE_EXMEM.RegWrite;

		// COPY MEMWB

		READ_MEMWB.MemToReg = WRITE_MEMWB.MemToReg;
		READ_MEMWB.RegWrite = WRITE_MEMWB.RegWrite;
		READ_MEMWB.LWDataValue = WRITE_MEMWB.LWDataValue;
		READ_MEMWB.ALU_Result = WRITE_MEMWB.ALU_Result;
		READ_MEMWB.WriteRegNumber = WRITE_MEMWB.WriteRegNumber;
		
		// COPY OVER IFID
		READ_IFID.Instruction = WRITE_IFID.Instruction;




	}	
}

