package Parse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import DBConection.MethodDAO;
import DBConection.MySQLConnection;
 
public class Main {
	
	private static List<MethodInformations> methods;
 
	public static void main(String[] args) throws IOException {
		//List<String> paths = getAllFiles("C:\\Users\\guga\\Documents\\Pesquisa\\Projetos\\ArgoUML-0.34-src");
		//List<String> paths = getAllFiles("C:\\Users\\guga\\Documents\\Pesquisa\\Projetos\\eclipse-sourceBuild-srcIncluded-3.6.1");
		//List<String> paths = getAllFiles("C:\\Users\\guga\\Documents\\Pesquisa\\Projetos\\ganttproject-2.0.10-src");
		//List<String> paths = getAllFiles("C:\\Users\\guga\\Documents\\Pesquisa\\Projetos\\jEdit-4.5.1");
		List<String> paths = getAllFiles("C:\\Users\\guga\\Documents\\Pesquisa\\Projetos\\xerces-2_11_0");
		
		if(paths.isEmpty()){
			System.out.println("No java class find");
		}else{
			methods = new ArrayList<>();
			
			//Gera compilationUnity de cada classe
			for(String path : paths){
				System.out.println("\n" + path);
				final CompilationUnit cu = getCompilationUnit(readFileToString(path));
				getMethods(cu, path);
			}
			
			//slava dados no BD
			MySQLConnection con = new MySQLConnection();
			MethodDAO md = new MethodDAO(con.getMySQLConnection());
			System.out.println(con.statusConnection());
			System.out.println(methods.size());
			int id = 1;
			for(MethodInformations mi : methods){
				mi.setId(id);
				System.out.println("Method: " + mi.getName() + " | Start Line: " + 
						mi.getStartLine() + " | End Line: " + mi.getEndLine() + " | Length: " + mi.getLength()
						+ " | Class: " + mi.getMethodClass());
				System.out.println(md.insert(mi));
				System.out.println(id + "/" + methods.size());
				id++;
			}
		}
	}
	
	//use ASTParse to parse string
	public static CompilationUnit getCompilationUnit(String str) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(str.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		return (CompilationUnit) parser.createAST(null);
 
	}
	
	//obter informações das variaveis a partir da AST
	public static void getVariables(CompilationUnit cu){
		cu.accept(new ASTVisitor() {
			 
			Set<String> names = new HashSet<String>();
 
			public boolean visit(VariableDeclarationFragment node) {
				SimpleName name = node.getName();
				this.names.add(name.getIdentifier());
				System.out.println("Declaration of '" + name + "' at line"
						+ cu.getLineNumber(name.getStartPosition()));
				return false; // do not continue 
			}
 
			public boolean visit(SimpleName node) {
				if (this.names.contains(node.getIdentifier())) {
					System.out.println("Usage of '" + node + "' at line "
							+ cu.getLineNumber(node.getStartPosition()));
				}
				return true;
			}
		});
	}
	
	//obter informações dos metodos a partir da AST
	public static void getMethods(CompilationUnit cu, String classPath){
		try{

		cu.accept(new ASTVisitor() {
			
			public boolean visit(MethodDeclaration node){
				SimpleName name = node.getName();
				
				try{
				int fristDeclarationCaracter = node.getBody().getStartPosition();
				int declarationLine = cu.getLineNumber(fristDeclarationCaracter);
				int charactersBody = node.getBody().getLength();
				int endLine = cu.getLineNumber(fristDeclarationCaracter + charactersBody);
				
				methods.add(new MethodInformations(name.getIdentifier(), classPath, declarationLine, endLine));
				
				}catch(NullPointerException e){
					System.out.println("Abstract method or interface");
				}
				return true;
			}
		});
		}catch(NullPointerException e){
			e.printStackTrace();
			System.out.println("NullPointerException!!!!!!");
		}
	}
	
	//obter todos arquivos de uma pasta
	public static List<String> getAllFiles(String folder){
		List<String> paths = new ArrayList<String>();
		Pattern patternJavaFile = Pattern.compile("((.*\\.java))");
		
		
		try {
			Files.walk(Paths.get(folder)).forEach(filePath -> {
			    if (Files.isRegularFile(filePath)) {
			    	String path = filePath.toString();
			    	Matcher matcher = patternJavaFile.matcher(path);
			    	if(matcher.matches()){
			    		paths.add(path);
			    	}   
			    }
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return paths;
	}
 
	//read file content into a string
	public static String readFileToString(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
 
		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			//System.out.println(numRead);
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
 
		reader.close();
 
		return  fileData.toString();	
	}
	
}