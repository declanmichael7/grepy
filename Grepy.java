import java.util.ArrayList;
import java.util.Stack;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class Grepy{
    public static void main(String[] args){
		ArrayList<Character> alphabet = new ArrayList<>();
		try {
			alphabet = Grepy.getAlphabet(args[1]);
		} catch(FileNotFoundException e) {
			System.out.println("File Not Found");
		} catch(IOException e) {
			System.out.println("File Not Found");
		}
		NFA nfa;
		nfa = Grepy.regexToNFA(args[0]);
		System.out.println("NFA:");
		System.out.println(nfa.getStates() + "\n" + nfa.getAcceptStates()); 
		int i=0;
		ArrayList<Transition> NFAtransitions = new ArrayList<>();
		NFAtransitions = nfa.getTransitions();
		while(i<NFAtransitions.size()){
            System.out.println(
                    NFAtransitions.get(i).getStateFrom() + "\t " +
                    NFAtransitions.get(i).getStateTo() + "\t" +
                    NFAtransitions.get(i).getCharacter());
			i++;
        }
		
    }
	public static NFA regexToNFA(String regex){
		ArrayList<Transition> transitions = new ArrayList<>();
        Transition transition;
		ArrayList<Integer> states = new ArrayList<>();
		ArrayList<Integer> acceptStates = new ArrayList<>();
		NFA nfa = new NFA(states, acceptStates, transitions);
		Stack<Integer> unionEnd = new Stack<>();
		int pos = 0;
		if(regex.indexOf('$') !=-1){
			regex = regex.substring(0,regex.indexOf('$'));
		}
		states.add(0);
		int currentState = 0;
		Stack<Integer> subExpStart = new Stack<>();
		int depth = 0;
		int i = 0;
		boolean unionFlag = false;
		while(i<regex.length()){
			if(regex.charAt(i) == '+' && depth == 0){
				unionFlag = true;
			}
			else if(regex.charAt(i) == '('){
				depth++;
			}
			else if(regex.charAt(i) == ')'){
				depth--;
			}
			i++;
		}
		if(unionFlag == true){
			subExpStart.push(currentState);
			states.add(currentState + 1);
			transition = new Transition(currentState, (currentState + 1), '!');
		    transitions.add(transition);
			currentState++;
		}
		while(pos<regex.length()){
			if(regex.charAt(pos) == '('){
				subExpStart.push(currentState);
				unionFlag = false;
				depth = -1;
				i=0;
				while(i<regex.length()){
					if(regex.charAt(i) == '+' && depth == 0){
						unionFlag = true;
					}
					else if(regex.charAt(i) == '('){
						depth++;
					}
					else if(regex.charAt(i) == ')'){
						depth--;
					}
					i++;
				}
				if(unionFlag == true){
					states.add(currentState + 1);
					transition = new Transition(currentState, (currentState + 1), '!');
					transitions.add(transition);
					currentState++;
				}
			}
			else if(regex.charAt(pos) == ')'){
				if(unionEnd.size()!=0){
					if(unionEnd.peek()==subExpStart.size()){
						unionEnd.pop();
						transition= new Transition(currentState, unionEnd.peek(), '!');
						transitions.add(transition);
						if(pos<regex.length()-1){
							if(regex.charAt(pos+1)=='*'){
								transition = new Transition(unionEnd.peek(), subExpStart.peek(), '!');
								transitions.add(transition);
								transition = new Transition(subExpStart.peek(), unionEnd.peek(), '!');
								transitions.add(transition);
								pos++;
							}
						}
						if(pos==regex.length()-1){
							acceptStates.add(unionEnd.pop());
						}
						else{
							states.add(currentState+1);
							transition = new Transition(unionEnd.peek(), currentState+1, '!');
							transitions.add(transition);
							unionEnd.pop();
							currentState++;
						}
					}
				}
				if(pos < regex.length()-1){
					if(regex.charAt(pos+1) == '*'){
						transition = new Transition(currentState, subExpStart.peek(), '!');
						transitions.add(transition);
						transition = new Transition(subExpStart.peek(), currentState, '!');
						transitions.add(transition);
						pos++;
					}
				}
				subExpStart.pop();
			}
			else if(regex.charAt(pos) == '*' && Character.isLetterOrDigit(regex.charAt(pos-1))){
					transition = new Transition(currentState, currentState-1, '!');
					transitions.add(transition);
					transition = new Transition(currentState-1, currentState, '!');
					transitions.add(transition);
			}
			else if(regex.charAt(pos) == '+'){
				unionFlag = false;
				depth = 0;
				i=pos-1;
				while(i>0){
					if(regex.charAt(i) == '+' && depth == 0){
						unionFlag = true;
					}
					else if(regex.charAt(i) == '('){
						depth--;
					}
					else if(regex.charAt(i) == ')'){
						depth++;
					}
					i--;
				}
				if(unionFlag==true){
					int temp = unionEnd.pop();
					transition = new Transition(currentState, unionEnd.peek(), '!');
					transitions.add(transition);
					unionEnd.push(temp);
					states.add(currentState+1);
					transition = new Transition(subExpStart.peek(), currentState+1, '!');
					transitions.add(transition);
					currentState++;
				}
				else{
					states.add(currentState+1);
					transition = new Transition(currentState, (currentState + 1), '!');
					transitions.add(transition);
					states.add(currentState+2);
					transition = new Transition(subExpStart.peek(), currentState+2, '!');
					transitions.add(transition);
					unionEnd.push(currentState+1);
					unionEnd.push(subExpStart.size());
					currentState+=2;
				}
			}
			else{
				states.add(currentState + 1);
				transition = new Transition(currentState,(currentState + 1),regex.charAt(pos));
				transitions.add(transition);
				currentState++;
			}
		    pos++;
		}
		if(unionEnd.size()!=0){
				unionEnd.pop();
				transition= new Transition(currentState, unionEnd.peek(), '!');
				transitions.add(transition);
				acceptStates.add(unionEnd.pop());
		}
		if(acceptStates.size()==0){
			acceptStates.add(currentState);
		}
		nfa.transitions = transitions;
		nfa.states = states;
		nfa.acceptStates = acceptStates;
		return nfa;
        }
    public static ArrayList<Character> getAlphabet(String filename) throws FileNotFoundException, IOException {
        String fileName = filename;
		ArrayList<Character> Alphabet = new ArrayList<Character>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileName), StandardCharsets.UTF_8));) {
				String line;
                while((line = br.readLine()) != null) {
					int i =0;
					while(i<line.length()){
					if(!Alphabet.contains(line.charAt(i))){
						Alphabet.add(line.charAt(i));
					}
					i++;
					}
				}
        }
		return Alphabet;
    }
}
class NFA{
	public ArrayList<Integer> states = new ArrayList<>();
	public ArrayList<Integer> acceptStates = new ArrayList<>();
	public ArrayList<Transition> transitions = new ArrayList<>();
	
	public NFA(ArrayList<Integer> states, ArrayList<Integer> acceptStates, ArrayList<Transition> transitions){
		this.states = states;
		this.acceptStates = acceptStates;
		this.transitions = transitions;
	}
	public ArrayList<Integer> getStates(){
		return states;
	}
	public ArrayList<Integer> getAcceptStates(){
		return acceptStates;
	}
	public ArrayList<Transition> getTransitions(){
		return transitions;
	}
}
class Transition{
    private final int stateFrom;
    private final int stateTo;
    private final char character;

    public Transition(int stateFrom, int stateTo, char character){
        this.stateFrom = stateFrom;
        this.stateTo = stateTo;
        this.character = character;
    }

    public int getStateTo(){
        return stateTo;
    }

    public int getStateFrom(){
        return stateFrom;
    }

    public char getCharacter(){
        return character;
    }

}