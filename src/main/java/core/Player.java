package core;

import org.jetbrains.annotations.Nullable;

public class Player {
    private Stone side;

    private ComputerLogic computerLogic;

    public Stone getSide() {
        return side;
    }

    public ComputerLogic getComputerLogic() {
        return computerLogic;
    }

    public void setComputerLogic(ComputerLogic computerLogic) {
        this.computerLogic = computerLogic;
    }

    @Nullable
    public Player(Stone side, ComputerLogic computerLogic) {
        this.side = side;
        this.computerLogic = computerLogic;
    }
}
