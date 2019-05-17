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
		DFA dfa;
		dfa = Grepy.nfaToDFA(nfa, alphabet);
		//System.out.println(Grepy.findEps(nfa, 0));
		System.out.println("DFA:");
		System.out.println(dfa.getStartState() + "\n" + dfa.getStates() + "\n" + dfa.getAcceptStates());
		int i=0;
		ArrayList<DFATransition> DFAtransitions = dfa.getTransitions();
		while(i<DFAtransitions.size()){
            System.out.println(
                    DFAtransitions.get(i).getStateFrom() + "\t " +
                    DFAtransitions.get(i).getStateTo() + "\t" +
                    DFAtransitions.get(i).getCharacter());
			i++;
        }
		System.out.println("NFA:");
		System.out.println(nfa.getStates() + "\n" + nfa.getAcceptStates()); 
		i=0;
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
	public static int findTransition(ArrayList<Transition> transitions, int state, Character c){
		int n = 0;
		int stateTo = -1;
		while(n<transitions.size()){
			if(transitions.get(n).getStateFrom() == state && transitions.get(n).getCharacter() == c){
				return transitions.get(n).getStateTo();
			}
			n++;
		}
		return stateTo;
	}
	public static ArrayList<Integer> findEps(NFA nfa, int state){
		ArrayList<Transition> NFAtransitions = nfa.getTransitions();
		ArrayList<Integer> epsStates = new ArrayList<>();
		epsStates.add(state);
		int k=0;
		boolean done = false;
		int arraySize = 0;
		while(!done){
			if(k>=NFAtransitions.size()-1){
				if(arraySize==epsStates.size()){
					done = true;
				}
				else{
					arraySize = epsStates.size();
					k=0;
				}				
			}
			if(epsStates.contains(NFAtransitions.get(k).getStateFrom()) && NFAtransitions.get(k).getCharacter() =='!'){
				if(!epsStates.contains(NFAtransitions.get(k).getStateTo())){
					epsStates.add(NFAtransitions.get(k).getStateTo());
				}
			}
			k++;
		}
		Collections.sort(epsStates);
		return epsStates;
	}
	public static DFA nfaToDFA(NFA nfa, ArrayList<Character> alphabet){

		ArrayList<Transition> NFAtransitions = nfa.getTransitions();
		ArrayList<Integer> NFAstates = nfa.getStates();
		ArrayList<Integer> NFAacceptStates = nfa.getAcceptStates();
		//DFA states are strings because I'm combining NFA states for the DFA states
		ArrayList<DFATransition> DFAtransitions = new ArrayList<>();
		ArrayList<String> DFAstates = new ArrayList<>();
		ArrayList<String> DFAacceptStates = new ArrayList<>();
		int m=0;
		//Trap state. Anything in the alphabet that comes from Trap, goes to Trap
		DFAstates.add("Trap");
		while(m<alphabet.size()){
			DFAtransitions.add(new DFATransition("Trap", "trap", alphabet.get(m)));
			m++;
		}
		m=0;
		ArrayList<Integer> epsStates = findEps(nfa, 0);
	    String newState = "";
		String startState;
		if(epsStates.size()>=2){
			int k=0;
			boolean accept = false;
			while(k<epsStates.size()){
				if(NFAacceptStates.contains(epsStates.get(k))){
					accept=true;
				}
				newState+= "c" + epsStates.get(k);
			k++;
			}
			if(accept==true){
				DFAacceptStates.add(newState);
			}
			DFAstates.add(newState);
			startState = newState;
			
		}
		else{
			if(NFAacceptStates.contains(epsStates.get(0))){
				DFAacceptStates.add(epsStates.get(0)+"");
			}
			DFAstates.add(epsStates.get(0)+"");
			startState = epsStates.get(0)+"";
		}
		int i=0;
		int k=0;
		int x=1;
		do{
			while(k<alphabet.size()){
				ArrayList<Integer> resultStates = new ArrayList<>();
				ArrayList<Integer> s = new ArrayList<>();
				Character c = alphabet.get(k);
				m=0;
				while(m<epsStates.size()){
					int j =0;
					int state = epsStates.get(m);
					boolean match = false;
					while(j < NFAtransitions.size() && match == false){
						Transition t = NFAtransitions.get(j);
						if(t.getStateFrom() == state && t.getCharacter() == c){
							resultStates.add(t.getStateTo());
							match = true;
						}
						else{
							j++;
						}
					}
					m++;
				}
				int l=0;
				while(l<resultStates.size()){
					ArrayList<Integer> r =(findEps(nfa, resultStates.get(l)));
					int p=0;
					while(p<r.size()){
						s.add(r.get(p));
						p++;
					}
					l++;
				}
				int p = 0;
				ArrayList<Integer> t = new ArrayList<>();
				while(p<s.size()){
					h=0;
					int transition=-1;
					boolean done = false;
					while(h<alphabet.size()&&transition==-1){
						transition = findTransition(NFAtransitions, s.get(p), alphabet.get(h));
						h++;
					}
					if(transition!=-1){
						ArrayList<Integer> q = findEps(nfa, transition);
						m=0;
						while(m<q.size()){
							t.add(q.get(m));
							m++;
						}
					}
					p++;
				}
				if(s.size()>=2){
					newState = "";
					m=0;
					boolean accept = false;
					while(m<s.size()){
						if(NFAacceptStates.contains(s.get(m))){
							accept=true;
						}
						newState+= "c" + s.get(m);
					m++;
					}
					if(!DFAstates.contains(newState)){
						DFAstates.add(newState);
					}
					if(accept){
						DFAacceptStates.add(newState);
					}
				}
				else if(s.size()==1){
					if(!DFAstates.contains(s.get(0)+"")){
						DFAstates.add(s.get(0)+"");
						if(NFAacceptStates.contains(s.get(0))){
							DFAacceptStates.add(s.get(0)+"");
						}
					}
				}
				if(t.size()>0){
					DFAstates.add(t.get(0)+"");
				}
				k++;
				s.clear();
			}
			x++;
			System.out.println(x +"\t"+DFAstates.size());
		}while(x<DFAstates.size());
		
	    DFA dfa = new DFA(DFAstates, DFAacceptStates, DFAtransitions, startState);
		return dfa;
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
class DFA{
	public ArrayList<String> states = new ArrayList<>();
	public ArrayList<String> acceptStates = new ArrayList<>();
	public ArrayList<DFATransition> transitions = new ArrayList<>();
	public String startState = new String();
	
	public DFA(ArrayList<String> states, ArrayList<String> acceptStates, ArrayList<DFATransition> transitions, String startState){
		this.states = states;
		this.acceptStates = acceptStates;
		this.transitions = transitions;
		this.startState = startState;
	}
	public String getStartState(){
		return startState;
	}
	public ArrayList<String> getStates(){
		return states;
	}
	public ArrayList<String> getAcceptStates(){
		return acceptStates;
	}
	public ArrayList<DFATransition> getTransitions(){
		return transitions;
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
class DFATransition{
    private final String stateFrom;
    private final String stateTo;
    private final char character;

    public DFATransition(String stateFrom, String stateTo, char character){
        this.stateFrom = stateFrom;
        this.stateTo = stateTo;
        this.character = character;
    }

    public String getStateTo(){
        return stateTo;
    }

    public String getStateFrom(){
        return stateFrom;
    }

    public char getCharacter(){
        return character;
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