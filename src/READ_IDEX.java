public class READ_IDEX {
	// Info

	static int ReadReg1Value;
	static int ReadReg2Value;
	static int SEOoffset;
	static int Write_Reg20_16;
	static int Write_Reg15_11;
	static int function;

	// CONTROL

	static int RegDest; // 0 if 16-20 bits for I-type vs 1 for bits 11-15 for R-Type
	static int ALUsrc; // false for R type, true for I type
	static int ALUop;   // 10 if R, 00 if load or store 
	static int MemRead; // true for I- type or false for R-type
	static int MemWrite; //
	static int RegWrite; // 1 for R type or True
	static int MemToReg; // 0 for R type

}


