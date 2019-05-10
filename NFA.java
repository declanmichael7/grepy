import java.util.ArrayList;

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
			regex = regex.substring(0,regex.length()-1);
		}
		/*
		//count how many +'s are in the regex so that our first state is zero
		int pluscount = 0;
	    for(int i = 0; int < regex.length(); i++){
			if(regex.charAt(i) == '+'){
				pluscount++;
			}
		}
		*/
		states.add(0);
		acceptStates.add(0);
		int currentState = 0;
		int nextState = 1;
		while(pos<regex.length()){
			if(Character.isLetterOrDigit(regex.charAt(pos))){
				states.add(nextState);
				transition = new Transition(currentState,nextState,regex.charAt(pos));
				transitions.add(transition);
				currentState++;
				nextState++;
				acceptStates.set(0, currentState);
			}
		pos++;
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