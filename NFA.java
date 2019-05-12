import java.util.ArrayList;
import java.util.Stack;

public class NFA{
    public static void main(String[] args){
		ArrayList<Transition> transitions = new ArrayList<>();
        Transition transition;
		ArrayList<Integer> states = new ArrayList<>();
		ArrayList<Integer> acceptStates = new ArrayList<>();
		Stack<Integer> unionEnd = new Stack<>();
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