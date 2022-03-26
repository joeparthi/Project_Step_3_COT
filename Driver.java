
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Stack;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Driver {
	
	public static void main(String[] args) throws Exception {
		
		
		//Standard input way as in book
		ANTLRInputStream input = new ANTLRInputStream(System.in);
		
		LittleLexer lexer = new LittleLexer(input);
		
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		
		LittleParser parser = new LittleParser(tokens);
		
		parser.removeErrorListeners();
		
		parser.addErrorListener(new VerboseListener());
		
		ParseTree tree = parser.program();
		
		//System.out.println(tree.toStringTree(parser));
		
		//parser.removeErrorListeners();
		
		//parser.addErrorListener(new VerboseListener());
		
		//parser.program();
		
		//System.out.println("\nAccepted\n");
		
	}
	
	class SymbolExtractor extends LittleBaseListener {
		
		private Stack<SymbolTable> symbol_table_stack;
		private Stack<SymbolTable> symbol_table_stack_seen;
		private SymbolTable current_table;
		private int block_count = 0;
		
		public SymbolExtractor() {
			this.symbol_table_stack = new Stack<SymbolTable>();
			this.current_table = null;
		}
		
		
		// Program Declarations _________________________________________
		@Override 
		public void enterProgram(LittleParser.ProgramContext ctx) { 
			
			this.symbol_table_stack.push(new SymbolTable("GLOBAL"));
			this.current_table = this.symbol_table_stack.peek();
			
		}
		
		@Override 
		public void exitProgram(LittleParser.ProgramContext ctx) {
			
			// For loop to iterate through print stack
			for (int stack_iterate = 0; stack_iterate < symbol_table_stack_seen.size(); stack_iterate++) {
				
				// Pop off stack to get current symbol table to work with
				current = symbol_table_stack_seen.pop();
				
				//Print current scope
				println("Symbol table " + current.getScope());
				
				// Iterate through arraylist to print symbols in current symbol table
				for(array_iterate = 0; array_iterate < current.symbolNames.size(); array_iterate++) {
					
					String symbol = current.symbolNames.get(array_iterate);
					
					String value = current.symbolTable.get(symbol);
					
					// If the type is a string, print name, type and value 
					// Else print name and type for other symbols
					if(value.getType() == "STRING") {
						println("name " + symbol + " type " + value.getType() + " value " + value.getValue());
					}
					
					else {
						
						println("name " + symbol + " type " + value.getType());
					
					}
					
				}
				
			}
			
		}
			
		
		
		
		// String Declarations _____________________________________________
		@Override 
		public void enterString_decl(LittleParser.String_declContext ctx) { 
			
			this.current_table.addSymbol(ctx.id().IDENTIFIER().getText(), new SymbolAttributes("STRING", ctx.str().STRINGLITERAL().getText()));
			
		}
		
		@Override 
		public void exitString_decl(LittleParser.String_declContext ctx) { 
		
		
		}
		
		// Variable Declarations ____________________________________
		@Override 
		public void enterVar_decl(LittleParser.Var_declContext ctx) { 
			String type = ctx.var_type().getText();
			
			String id_list_string = ctx.id_list().getText();
			String[] id_list = id_list_string.split(",");
			
			for(int id_iterate = 0; id_iterate < id_list.length; id_iterate++) {
				
				this.current_table.addSymbol(id_list[id_iterate], new SymbolAttributes(type);
				
			}
		}

		@Override 
		public void exitVar_decl(LittleParser.Var_declContext ctx) { 
		
		
		}
		
		// Function Parameter Declarations ______________________________
		@Override 
		public void enterParam_decl(LittleParser.Param_declContext ctx) { 
			//this.current_table.addSymbol(ctx.id().IDENTIFIER().getText(), new SymbolAttributes(ctx.var_type().getText());
			
			String decl_list_str = ctx.param_decl_list().getText();
			String[] decl_list_with_space = decl_list_str.split(",");
			String[] decl_list = decl_list_with_space.split(" ");
			
			for(int i = 0; i < decl_list.length - 2; i = i + 2) {
				
				this.current_table.addSymbol(decl_list[i + 1], new SymbolAttributes(decl_list[i]);
			}
			
			//FUNCTION VOID main(INT a, INT b, FLOAT c)
			//decl_list = [INT a, INT b, FLOAT c];
		
		}

		@Override 
		public void exitParam_decl(LittleParser.Param_declContext ctx) { 
		
		
		}
		
		// Function Declarations__________________________________________________
		@Override 
		public void enterFunc_decl(LittleParser.Func_declContext ctx) {
			
			// this.current_table.addSymbol(ctx.id().IDENTIFIER().getText(), new SymbolAttributes("FUNCTION");
			this.current_table.addSymbol(ctx.id().IDENTIFIER().getText(), new SymbolAttributes("FUNCTION");
			this.symbol_table_stack.push(new SymbolTable(ctx.id().IDENTIFIER().getText()));
			this.current_table = this.symbol_table_stack.peek();
	
		
		}

		@Override 
		public void exitFunc_decl(LittleParser.Func_declContext ctx) { 
			this.symbol_table_stack_seen.pop(this.symbol_table_stack.pop());
		
		}
	
		@Override public void enterWhile_stmt(LittleParser.While_stmtContext ctx) {
			block_count++;
			this.symbol_table_stack.push(new SymbolTable("BLOCK " + block_count));
			this.current_table = this.symbol_table_stack.peek();
		 }

		@Override public void exitWhile_stmt(LittleParser.While_stmtContext ctx) { 
			
			// Pop from ST stack and push onto print stack for printing
			this.symbol_table_stack_seen.push(this.symbol_table_stack.pop());
		}

		@Override public void enterIf_stmt(LittleParser.If_stmtContext ctx) {
			block_count++;
			this.symbol_table_stack.push(new SymbolTable("BLOCK" + block_count));
			this.current_table = this.symbol_table_stack.peek();
		 }

		@Override public void exitIf_stmt(LittleParser.If_stmtContext ctx) { 
			this.symbol_table_stack_seen.pop(this.symbol_table_stack.pop());
		}

		@Override public void enterElse_part(LittleParser.Else_partContext ctx) {
				block_count++;
				this.symbol_table_stack.push(new SymbolTable("BLOCK" + block_count));
				this.current_table = this.symbol_table_stack.peek();
		 }

		@Override public void exitElse_part(LittleParser.Else_partContext ctx) {
			this.symbol_table_stack_seen.pop(this.symbol_table_stack.pop());
		 }
		
	}
	
	class SymbolTable {
		
		private String scope;
		
		private HashMap<String, SymbolAttributes> symbolTable;
		
		private ArrayList<String> symbolNames;
		
		public SymbolTable(String scope) {
			this.scope = scope;
			this.symbolTable = new HashMap<String, SymbolAttributes>();
			this.symbolNames = new ArrayList<String>(); 
		}
		
		public String getScope() {
			return this.scope;
		}
		
		public void addSymbol(String name, SymbolAttributes attr) {
			
			if(this.symbolTable.containsKey(name)) {
				
				System.out.printf("DECLARATION ERROR %s\n", name);
				System.exit(0);
				
			}
			
			this.symbolTable.put(name, attr);
			this.symbolNames.add(name);
			//this.symbolTable.get(name).getValue().getType()
		}
		
	}
	
	
	class SymbolAttributes {
		String type;
		String value;
		
		public SymbolAttributes (String type, String value) {
			this.type = type;
			this.value = value;
		}
		
		public String getType() {
			return this.type;
		}
		
		public String getValue() {
			return this.value;
		}
	}
	
	public static class VerboseListener extends BaseErrorListener {
		@Override
		public void syntaxError(Recognizer<?, ?> recognizer,
			Object offendingSymbol,
			int line, int charPositionInLine,
			String msg,
			RecognitionException e)
		{
			List<String> stack = ((Parser)recognizer).getRuleInvocationStack();
			
			Collections.reverse(stack);
			
			if(stack.size() > 0) {
				// System.out.println("\nNot accepted\n");
				System.exit(1);
			}
			/*
			System.err.println("rule stack: "+stack);
			System.err.println("line "+line+":"+charPositionInLine+" at "+
			offendingSymbol+": "+msg);
			*/
		}
	}
}
