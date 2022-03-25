
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
		
		System.out.println("\nAccepted\n");
		
	}
	
	class SymbolExtractor extends LittleBaseListener {
		
		private Stack<SymbolTable> symbol_table_stack;
		private SymbolTable current_table;
		
		public SymbolExtractor() {
			this.symbol_table_stack = new Stack<SymbolTable>();
			this.current_table = null;
		}
		
		@Override 
		public void enterProgram(LittleParser.ProgramContext ctx) { 
			
			this.symbol_table_stack.push(new SymbolTable("GLOBAL"));
			this.current_table = this.symbol_table_stack.peek();
			
		}
		
		@Override 
		public void exitProgram(LittleParser.ProgramContext ctx) { }
		
		
		
		
		// String declaration
		@Override 
		public void enterString_decl(LittleParser.String_declContext ctx) { 
			
			this.current_table.addSymbol(ctx.id().IDENTIFIER().getText(), new SymbolAttributes("STRING", ctx.str().STRINGLITERAL().getText()));
			
		}
		
		@Override public void exitString_decl(LittleParser.String_declContext ctx) { }
		
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
				System.out.println("\nNot accepted\n");
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
