import java.util.Scanner;
import java.util.Stack;

public class Driver {
	
	static int[] MainMem = new int[1024];
	static int[] Regs = new int[32];
	public static Stack<Integer> instructionCache = new Stack<Integer>();

	public static void main(String[] args) {
		Scanner kb = new Scanner(System.in);
		
		
		// INITIALIZE BLOCK
		setIns();
		setMem();
		setRegs();
		
		// ONE CYCLE BLOCK
		boolean run = true;
		while(!instructionCache.isEmpty()){
		IF_stage();
		ID_stage();		
		EX_stage();
		MEM_stage(); 		
		WB_stage(); 		
		Print_out_everything(); 
		Copy_write_to_read();
		System.out.println("Press any key to next cycle");
		kb.nextLine();
		}
		System.out.println("Complete.");
			
	}
	///
	
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
			System.out.println("Register " + i + " " + Regs[i]);
		}
	}

	public static void showPipeReg(){
		System.out.println("*** IFIF ***");
		System.out.println("WRITE:");
		System.out.println("Instruction: " + Integer.toHexString(WRITE_IFID.Instruction));
		System.out.println("READ:");
		System.out.println("Instruction: " + Integer.toHexString(READ_IFID.Instruction));
		System.out.println("_______________\n");
		System.out.println("*** IDEX ***");
		System.out.println("WRITE");
		System.out.println("Instruction: " + Integer.toHexString(WRITE_IDEX.Instruction));
		System.out.println("OP Code: " + WRITE_IDEX.op_Code);
		System.out.println("RS register: " + WRITE_IDEX.rs_REGISTER);
		System.out.println("RS register Value: " + WRITE_IDEX.rs_REGISTER_VALUE);
		System.out.println("RT register: " + WRITE_IDEX.rt_REGISTER);
		System.out.println("RT register Value: " + WRITE_IDEX.rt_REGISTER_VALUE);
		System.out.println("RD register: " + WRITE_IDEX.rd_REGISTER);
		System.out.println("RD register Value: " + WRITE_IDEX.rd_REGISTER_VALUE);
		System.out.println("Control: Is I Type: " + WRITE_IDEX.isItype);
		System.out.println("Offset " + WRITE_IDEX.offset_IType);
		System.out.println("READ");
		System.out.println("Instruction: " + Integer.toHexString(READ_IDEX.Instruction));
		System.out.println("OP Code: " + READ_IDEX.op_Code);
		System.out.println("RS register: " + READ_IDEX.rs_REGISTER);
		System.out.println("RS register Value: " + READ_IDEX.rs_REGISTER_VALUE);
		System.out.println("RT register: " + READ_IDEX.rt_REGISTER);
		System.out.println("RT register Value: " + READ_IDEX.rt_REGISTER_VALUE);
		System.out.println("RD register: " + READ_IDEX.rd_REGISTER);
		System.out.println("RD register Value: " + READ_IDEX.rd_REGISTER_VALUE);
		System.out.println("Control: Is I Type: " + READ_IDEX.isItype);
		System.out.println("Offset: " + READ_IDEX.offset_IType);
		System.out.println("_______________\n");
		System.out.println("*** EXMEM ***");
		System.out.println("WRITE");
		System.out.println("Instruction: " + Integer.toHexString(WRITE_EXMEM.Instruction));
		System.out.println("OP Code: " + WRITE_EXMEM.op_Code);
		System.out.println("RS register: " + WRITE_EXMEM.rs_REGISTER);
		System.out.println("RS register Value: " + WRITE_EXMEM.rs_REGISTER_VALUE);
		System.out.println("RT register: " + WRITE_EXMEM.rt_REGISTER);
		System.out.println("RT register Value: " + WRITE_EXMEM.rt_REGISTER_VALUE);
		System.out.println("RD register: " + WRITE_EXMEM.rd_REGISTER);
		System.out.println("RD register Value: " + WRITE_EXMEM.rd_REGISTER_VALUE);
		System.out.println("Control: Is I Type " + WRITE_EXMEM.isItype);
		System.out.println("Offset: " + WRITE_EXMEM.offset_IType);	
		System.out.println("ALU_RESULT: " + WRITE_EXMEM.ALU_RESULT);
		System.out.println("READ");
		System.out.println("Instruction: " + Integer.toHexString(READ_EXMEM.Instruction));
		System.out.println("OP Code: " + READ_EXMEM.op_Code);
		System.out.println("RS register: " + READ_EXMEM.rs_REGISTER);
		System.out.println("RS register Value: " + READ_EXMEM.rs_REGISTER_VALUE);
		System.out.println("RT register: " + READ_EXMEM.rt_REGISTER);
		System.out.println("RT register Value: " + READ_EXMEM.rt_REGISTER_VALUE);
		System.out.println("RD register: " + READ_EXMEM.rd_REGISTER);
		System.out.println("RD register Value: " + READ_EXMEM.rd_REGISTER_VALUE);
		System.out.println("Control: Is I Type " + READ_EXMEM.isItype);
		System.out.println("Offset: " + READ_EXMEM.offset_IType);	
		System.out.println("ALU_RESULT: " + READ_EXMEM.ALU_RESULT);
		System.out.println("_______________\n");
		System.out.println("*** MEM_WB ***");
		System.out.println("WRITE");
		System.out.println("Instruction: " + Integer.toHexString(WRITE_MEMWB.Instruction));
		System.out.println("OP Code: " + WRITE_MEMWB.op_Code);
		System.out.println("RS register: " + WRITE_MEMWB.rs_REGISTER);
		System.out.println("RS register Value: " + WRITE_MEMWB.rs_REGISTER_VALUE);
		System.out.println("RT register: " + WRITE_MEMWB.rt_REGISTER);
		System.out.println("RT register Value: " + WRITE_MEMWB.rt_REGISTER_VALUE);
		System.out.println("RD register: " + WRITE_MEMWB.rd_REGISTER);
		System.out.println("RD register Value: " + WRITE_MEMWB.rd_REGISTER_VALUE);
		System.out.println("Control (Is I Type): " + WRITE_MEMWB.isItype);
		System.out.println("Offset: " + WRITE_MEMWB.offset_IType);	
		System.out.println("ALU_RESULT: " + WRITE_MEMWB.ALU_RESULT);
		System.out.println("Value from mem " + WRITE_MEMWB.ValueFromMemory);
		System.out.println("READ");
		System.out.println("Instruction: " + Integer.toHexString(READ_MEMWB.Instruction));
		System.out.println("OP Code: " + READ_MEMWB.op_Code);
		System.out.println("RS register: " + READ_MEMWB.rs_REGISTER);
		System.out.println("RS register Value: " + READ_MEMWB.rs_REGISTER_VALUE);
		System.out.println("RT register: " + READ_MEMWB.rt_REGISTER);
		System.out.println("RT register Value: " + READ_MEMWB.rt_REGISTER_VALUE);
		System.out.println("RD register: " + READ_MEMWB.rd_REGISTER);
		System.out.println("RD register Value: " + READ_MEMWB.rd_REGISTER_VALUE);
		System.out.println("Control (Is I Type): " + READ_MEMWB.isItype);
		System.out.println("Offset: " + READ_MEMWB.offset_IType);	
		System.out.println("ALU_RESULT: " + READ_MEMWB.ALU_RESULT);
		System.out.println("Value from mem: " + READ_MEMWB.ValueFromMemory);

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
	
	private static void showMem() {
		for (int i = 0; i < 1024; i++) {
			System.out.println(MainMem[i]+ " ");	
			}
			
	
	}
	
	public static void IF_stage() {
		// Read instruction from PC counter, it will increment
		WRITE_IFID.Instruction = instructionCache.pop();
		// Pass write value to read value
		READ_IFID.Instruction = WRITE_IFID.Instruction;
	}
	
	public static void ID_stage() {
		// Here you'll read an instruction from the READ version of IF/ID pipeline register
		int Instruction = READ_IFID.Instruction;
		//do the decoding 	
		int rs_REGISTER_VALUE;
		int rs_REGISTER;	
		int rt_REGISTER_VALUE;
		int rt_REGISTER;
		int rd_REGISTER_VALUE= 0;
		int rd_REGISTER = 0;
		boolean isItype = false;
		short offset_IType = 0;
		int op_Code;

		String temp = Integer.toBinaryString(Instruction);
		// Make sure binary string is 32 bits, add zeros if needed
		for (int i = temp.length(); i != 32; i++){
			temp = "0" + temp;
		} 
		
		//and register fetching 
		
		// SEND to R-Type if beginning with 000000
		if (temp.substring(0,6).contains("000000")){
			
			String rs = temp.substring(6,11);
			int rsAddress = Integer.parseInt(rs,2); // GET RS REGISTER NUMBER
			rs_REGISTER = rsAddress;
			rs_REGISTER_VALUE = Regs[rsAddress];   // GET VALUE		
					
			String rt = (temp.substring(11,16));
			int rtAddress = Integer.parseInt(rt,2); // GET RT REGISTER NUMBER
			rt_REGISTER = rtAddress;
			rt_REGISTER_VALUE = Regs[rtAddress];   // GET VALUE
			
			String rd = (temp.substring(16,21));
			int rdAddress = Integer.parseInt(rd,2); // GET RD REGISTER NUMBER
			rd_REGISTER =  rdAddress;
			rd_REGISTER_VALUE = Regs[rdAddress]; // GET VALUE
			
			String funct = temp.substring(26,32);
			op_Code = Integer.parseInt(funct, 2);
			
			
			isItype = false;
		}
		
		// SEND TO I TYPE INSTRUCTION
		else {
			String offSetasString = null;
			
			String op = temp.substring(0,6);
			op_Code = Integer.parseInt(op,2);
			
			String rs = temp.substring(6,11);
			int rsAddress = Integer.parseInt(rs,2); // GET RS REGISTER NUMBER
			rs_REGISTER = rsAddress;
			rs_REGISTER_VALUE = Regs[rsAddress]; // GET VALUE
			
			String rt = (temp.substring(11,16));
			int rtAddress = Integer.parseInt(rt,2);
			rt_REGISTER = rtAddress;
			rt_REGISTER_VALUE = Regs[rtAddress];   // GET VALUE
			
			//Get offset address
			offSetasString = temp.substring(16);
			offset_IType = (short) Long.parseLong(offSetasString,2); // GET OFFSET
			
			isItype = true;
			
		}
		
		//and write the values to the WRITE version of the ID/EX pipeline register.
		
		WRITE_IDEX.Instruction = Instruction;
		WRITE_IDEX.rs_REGISTER_VALUE = rs_REGISTER_VALUE;
		WRITE_IDEX.rs_REGISTER = rs_REGISTER;
		WRITE_IDEX.rt_REGISTER_VALUE = rt_REGISTER_VALUE;
		WRITE_IDEX.rt_REGISTER = rt_REGISTER;
		WRITE_IDEX.rd_REGISTER_VALUE = rd_REGISTER_VALUE;
		WRITE_IDEX.rd_REGISTER = rd_REGISTER;
		WRITE_IDEX.isItype = isItype;
		WRITE_IDEX.offset_IType = offset_IType;
		WRITE_IDEX.op_Code = op_Code;
		
		
	}

	private static void EX_stage() {
		// Here you'll perform the requested instruction on the specific operands you read 
		//out of the READ version of the IDEX pipeline register 
		int Instruction = READ_IDEX.Instruction;
		//do the decoding 	
		int rs_REGISTER_VALUE= READ_IDEX.rs_REGISTER_VALUE;
		int rs_REGISTER = READ_IDEX.rs_REGISTER;	
		int rt_REGISTER_VALUE = READ_IDEX.rt_REGISTER_VALUE;
		int rt_REGISTER = READ_IDEX.rt_REGISTER;
		int rd_REGISTER_VALUE = READ_IDEX.rd_REGISTER_VALUE ;
		int rd_REGISTER = READ_IDEX.rd_REGISTER;
		boolean isItype = READ_IDEX.isItype;
		short offset_IType = READ_IDEX.offset_IType;
		int op_Code = READ_IDEX.op_Code;
		int ALU_RESULT = 0;
		
		
		
		
		
		// Here is control

		// IF IT IS R-TYPE
		if(!isItype){

			switch(op_Code){

			case (0x20):{ // ADD
				// RD = RS + RT
				ALU_RESULT = rs_REGISTER_VALUE + rt_REGISTER_VALUE;
				break;
			}
			case (0x22):{ // SUB
				ALU_RESULT = rs_REGISTER_VALUE - rt_REGISTER_VALUE;
				break;
			}

			case (0):{ // NOP
				break;	
			}
			}
		}
		
		
		else{			
			
			switch(op_Code){

			case (0x20):{ // LOAD BYTE
				// RT = RS(+Offest)
				ALU_RESULT = MainMem[rs_REGISTER_VALUE + offset_IType];
				break;
			}
			case (0x28):{ // STORE BYTE
				// 
				ALU_RESULT = rs_REGISTER_VALUE + offset_IType;
				break;
			}

			case (0):{ // NOP
				break;	
			}
			}			
		}

		
		// and then write the appropriate values to the WRITE version of the EX/MEM pipeline register.
		WRITE_EXMEM.Instruction = Instruction;
		WRITE_EXMEM.rs_REGISTER_VALUE= rs_REGISTER_VALUE;
		WRITE_EXMEM.rs_REGISTER = rs_REGISTER;
		WRITE_EXMEM.rt_REGISTER_VALUE= rt_REGISTER_VALUE;
		WRITE_EXMEM.rt_REGISTER= rt_REGISTER;
		WRITE_EXMEM.rd_REGISTER_VALUE= rd_REGISTER_VALUE;
		WRITE_EXMEM.rd_REGISTER = rd_REGISTER;
		WRITE_EXMEM.isItype = isItype;
		WRITE_EXMEM.offset_IType = offset_IType;
		WRITE_EXMEM.op_Code= op_Code;
		WRITE_EXMEM.ALU_RESULT = ALU_RESULT;

		

		// EX_MEM_WRITE.ALU_Result = ID_EX_READ.Reg_Val1 + ID_EX_READ.Reg_Val2;
		

		
	}
	
	private static void MEM_stage() {
		//  MEM If the instruction is a lb, then use the address you calculated in the EX stage
		//as an index into your Main Memory array and get the value that is there. 
		int ValueFromMemory=0;
		
		if (READ_EXMEM.op_Code == 0x20){
			
			ValueFromMemory = MainMem[READ_EXMEM.ALU_RESULT];
						
		}
		
		// Otherwise, just pass information from the READ version of the EX_MEM pipeline register to the WRITE version of MEM_WB.
	
			WRITE_MEMWB.Instruction = READ_EXMEM.Instruction;
			WRITE_MEMWB.rs_REGISTER_VALUE= READ_EXMEM.rs_REGISTER_VALUE;
			WRITE_MEMWB.rs_REGISTER = READ_EXMEM.rs_REGISTER;
			WRITE_MEMWB.rt_REGISTER_VALUE= READ_EXMEM.rt_REGISTER_VALUE;
			WRITE_MEMWB.rt_REGISTER = READ_EXMEM.rt_REGISTER;
			WRITE_MEMWB.rd_REGISTER_VALUE= READ_EXMEM.rd_REGISTER_VALUE;
			WRITE_MEMWB.rd_REGISTER = READ_EXMEM.rd_REGISTER;
			WRITE_MEMWB.isItype = READ_EXMEM.isItype;
			WRITE_MEMWB.offset_IType = READ_EXMEM.offset_IType;
			WRITE_MEMWB.op_Code= READ_EXMEM.op_Code;
			WRITE_MEMWB.ALU_RESULT = READ_EXMEM.ALU_RESULT;
			WRITE_MEMWB.ValueFromMemory = ValueFromMemory;
	}
	
	private static void WB_stage() {
	// Write to the registers based on information you read out of the READ version of MEM_WB.
	
		if(!READ_MEMWB.isItype){

			switch(READ_MEMWB.op_Code){

			case (0x20):{ // ADD
				// RD = RS + RT
				Regs[READ_MEMWB.rd_REGISTER] = READ_MEMWB.ALU_RESULT;
				break;
			}
			case (0x22):{ // SUB
				Regs[READ_MEMWB.rd_REGISTER] = READ_MEMWB.ALU_RESULT;
				break;
			}

			case (0):{ // NOP
				break;	
			}
			}
		}
		
		
		else{			
			
			switch(READ_MEMWB.op_Code){

			case (0x20):{ // LOAD BYTE
				// RT = RS(+Offest)
				Regs[READ_MEMWB.rt_REGISTER] = READ_MEMWB.ALU_RESULT;
				
				break;
			}
			case (0x28):{ // Sb
				//// STORE BYTE
				MainMem[READ_MEMWB.ALU_RESULT] = Regs[READ_MEMWB.rt_REGISTER];
				break;
			}

			case (0):{ // NOP
				break;	
			}
			}			
		}	
	}
		
	private static void Print_out_everything() {
		System.out.println("Registers:");
		showRegs();
		System.out.println();
		System.out.println("Pipeline Reg Values\n");
		showPipeReg();
	}

	private static void Copy_write_to_read() {
		// 
		
		// COPY OVER IFID
		READ_IFID.Instruction = WRITE_IFID.Instruction;
		
		// COPY OVER IDEX
		READ_IDEX.Instruction = WRITE_IDEX.Instruction;
		READ_IDEX.rs_REGISTER_VALUE = WRITE_IDEX.rs_REGISTER_VALUE;
		READ_IDEX.rs_REGISTER = WRITE_IDEX.rs_REGISTER;
		READ_IDEX.rt_REGISTER_VALUE = WRITE_IDEX.rt_REGISTER_VALUE;
		READ_IDEX.rt_REGISTER = WRITE_IDEX.rt_REGISTER;
		READ_IDEX.rd_REGISTER_VALUE = WRITE_IDEX.rd_REGISTER_VALUE;
		READ_IDEX.rd_REGISTER = WRITE_IDEX.rd_REGISTER;
		READ_IDEX.isItype = WRITE_IDEX.isItype;
		READ_IDEX.offset_IType = WRITE_IDEX.offset_IType;
		READ_IDEX.op_Code = WRITE_IDEX.op_Code;
		
		// COPY OVER EXMEM
		READ_EXMEM.Instruction = WRITE_EXMEM.Instruction;
		READ_EXMEM.rs_REGISTER_VALUE = WRITE_EXMEM.rs_REGISTER_VALUE;
		READ_EXMEM.rs_REGISTER = WRITE_EXMEM.rs_REGISTER;
		READ_EXMEM.rt_REGISTER_VALUE = WRITE_EXMEM.rt_REGISTER_VALUE;
		READ_EXMEM.rt_REGISTER = WRITE_EXMEM.rt_REGISTER;
		READ_EXMEM.rd_REGISTER_VALUE = WRITE_EXMEM.rd_REGISTER_VALUE;
		READ_EXMEM.rd_REGISTER = WRITE_EXMEM.rd_REGISTER;
		READ_EXMEM.isItype = WRITE_EXMEM.isItype;
		READ_EXMEM.offset_IType = WRITE_EXMEM.offset_IType;
		READ_EXMEM.op_Code = WRITE_EXMEM.op_Code;
		READ_EXMEM.ALU_RESULT = WRITE_EXMEM.ALU_RESULT;
		
		// COPY OVER MEM-WB
		READ_MEMWB.Instruction = WRITE_MEMWB.Instruction;
		READ_MEMWB.rs_REGISTER_VALUE = WRITE_MEMWB.rs_REGISTER_VALUE;
		READ_MEMWB.rs_REGISTER = WRITE_MEMWB.rs_REGISTER;
		READ_MEMWB.rt_REGISTER_VALUE = WRITE_MEMWB.rt_REGISTER_VALUE;
		READ_MEMWB.rt_REGISTER = WRITE_MEMWB.rt_REGISTER;
		READ_MEMWB.rd_REGISTER_VALUE = WRITE_MEMWB.rd_REGISTER_VALUE;
		READ_MEMWB.rd_REGISTER = WRITE_MEMWB.rd_REGISTER;
		READ_MEMWB.isItype = WRITE_MEMWB.isItype;
		READ_MEMWB.offset_IType = WRITE_MEMWB.offset_IType;
		READ_MEMWB.op_Code = WRITE_MEMWB.op_Code;
		READ_MEMWB.ALU_RESULT = WRITE_MEMWB.ALU_RESULT;
		READ_MEMWB.ValueFromMemory = WRITE_MEMWB.ValueFromMemory;

		
	}	
}

