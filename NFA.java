import java.util.ArrayList;
import java.util.Stack;

public class NFA{
    public static void main(String[] args){
		ArrayList<Transition> transitions = new ArrayList<>();
        Transition transition;
		ArrayList<Integer> states = new ArrayList<>();
		ArrayList<Integer> acceptStates = new ArrayList<>();
		String regex = args[0];
		int pos = 0;
		if(regex.charAt(regex.length()-1) != '$'){
			System.out.println("Bad regex");
		}
		else{
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
			if(Character.isLetterOrDigit(regex.charAt(pos))){
				states.add(currentState + 1);
				transition = new Transition(currentState,(currentState + 1),regex.charAt(pos));
				transitions.add(transition);
				currentState++;
				if(acceptStates.size() == 0){
					acceptStates.add(currentState);
				}
				else{
					acceptStates.set(0, currentState);
				}
			}
			else if(regex.charAt(pos) == '('){
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
				if(pos+1<regex.length()){
					if(regex.charAt(pos+1) == '*'){
						transition = new Transition(currentState, subExpStart.peek(), '!');
						transitions.add(transition);
						transition = new Transition(subExpStart.peek(), currentState, '!');
						transitions.add(transition);
						pos++;
					}
					subExpStart.pop();
				}
			}
			else if(regex.charAt(pos) == '*' && Character.isLetterOrDigit(regex.charAt(pos-1))){
					transition = new Transition(currentState, currentState-1, '!');
					transitions.add(transition);
					transition = new Transition(currentState-1, currentState, '!');
					transitions.add(transition);
			}
			else if(regex.charAt(pos) == '+'){
				states.add((currentState + 1));
				transition = new Transition(subExpStart.peek(), (currentState + 1), '!');
				transitions.add(transition);
				if(subExpStart.size()==1){
					acceptStates.add(currentState);
				}
				currentState++;
			}
		    pos++;
		}
		if(acceptStates.size()==0){
			acceptStates.add(0);
		}
		System.out.println(states);
		System.out.println(acceptStates);
        for (Transition printTransition : transitions){
            System.out.println(
                    printTransition.getStateFrom() + "\t " +
                    printTransition.getStateTo() + "\t" +
                    printTransition.getCharacter());
        }
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