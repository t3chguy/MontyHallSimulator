import java.util.Random;

class MontyHallGame
{
	private static final boolean DEBUG = System.getenv("DEBUG") != null && System.getenv("DEBUG").equals("TRUE");

	private int chosenDoorIndex;
	private final int numberOfDoors;

	// Zero-indexed 0..n-1
	private boolean getBit(long value, int index) { return ((value >>> index) & 0x1) == 0x1; }

	private Random random = new Random();
	private int smartRandomInt(DoorValue... ignoredValues) { return smartRandomInt(-1, ignoredValues); }
	private int smartRandomInt(int ignoredIndex, DoorValue... ignoredValues)
	{
		int randomInt;
		do
		{
			randomInt = random.nextInt(numberOfDoors);
			for (int i = 0; i < ignoredValues.length; i++) {
				if (doors[randomInt] == ignoredValues[i])
				{
					randomInt = -1;
					break;
				}
			}
		} while (randomInt == ignoredIndex || randomInt < 0);
		return randomInt;
	}

	private enum DoorValue { GOAT, CAR, GOAT_OPEN }
	private final DoorValue[] doors;

	public MontyHallGame(int numberOfDoors)
	{
		this.numberOfDoors = numberOfDoors;
		this.doors = new DoorValue[numberOfDoors];
	}

	public void debug(String format, Object... parameters) { if (DEBUG) System.out.printf("DEBUG::"+ format, parameters); }

	public boolean runGame(long strategy)
	{
		int carDoorIndex = random.nextInt(numberOfDoors);
		chosenDoorIndex = smartRandomInt();

		for (int i = 0; i < numberOfDoors; i++)
			doors[i] = DoorValue.GOAT;
		doors[carDoorIndex] = DoorValue.CAR;

		for (int move = numberOfDoors - 3; move >= 0; move--)
        {
            debug(""+ move);
            oneRound( getBit(strategy, move) );
        }

        debugDoors();

		return doors[chosenDoorIndex] == DoorValue.CAR;
	}

    private void debugDoors()
    {
        if (DEBUG)
        {
            String buffer = "\t";
            for (int i = 0; i < numberOfDoors; i++)
                buffer += String.format(i == chosenDoorIndex ? "%15s*":"%16s", doors[i]);
            debug(buffer + "%n");
        }
    }

	private void oneRound(boolean shouldSwitch)
	{
		debugDoors();
        debug("%b%n%n", shouldSwitch);


		int montyDoor = smartRandomInt(chosenDoorIndex, DoorValue.CAR, DoorValue.GOAT_OPEN);
		doors[montyDoor] = DoorValue.GOAT_OPEN;

		if (shouldSwitch)
			chosenDoorIndex = smartRandomInt(chosenDoorIndex, DoorValue.GOAT_OPEN);
	}
}

public class MontyHallSimulator
{
	public static void main(String[] args)
	{
        final int numberOfDoors = args.length > 0 ? Integer.parseInt(args[0]) : 3;
        final int numberOfTests = args.length > 1 ? Integer.parseInt(args[1]) : 1;

        final int maxStrategy = (int)Math.pow(2, numberOfDoors - 2);
        int[] strategyWins = new int[maxStrategy];

        for (int test = 0; test < numberOfTests; test++)
        {
            MontyHallGame game = new MontyHallGame(numberOfDoors);
            for (int strategy = 0; strategy < maxStrategy; strategy++)
            {
                boolean result = game.runGame(strategy);
//                System.out.printf("%s %n%n", result ? "Win":"Loss");
                if (result) strategyWins[strategy]++;
                System.out.println("\n");

            }
        }

//        System.out.println();
        for (int i = 0; i < maxStrategy; i++)
        {
            String binaryString = Integer.toBinaryString(i);
            System.out.printf("%s resulted in %d wins (%1.2f%%)%n",
                    binaryString, strategyWins[i], 100* strategyWins[i] / (double)numberOfTests);
        }
    }
}