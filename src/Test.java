import matrix.*;
import utils.Size;

import java.util.*;


public class Test {
    public static void main(String[] args) {
//        Slice slice = SliceUtils.createSlice(2, 5);
//        for (int i : slice) {
//            System.out.println(i);
//        }
//        for (int i : SliceUtils.modifyStep(slice, 2)) {
//            System.out.println(i);
//        }
//        for (int i: SliceUtils.createSlice(2, 5, 2)) {
//            System.out.println(i);
//        }
//        Size size = Size.sizeOf(3, 3);
//        System.out.println(size);
//
//        size = size.scale(2);
//        System.out.println(size);
//
//        size = size.plus(Size.sizeOf(3, 3));
//        System.out.println(size);
//
//        size = size.minus(Size.sizeOf(3, 3));
//        System.out.println(size);
//
//        IntVector vect = new IntVector(5, ((i) -> i));
//        System.out.println(vect);
//
//        DoubleVector vec = new DoubleVector(5, (i) -> (double) i);
//
//        vect = VectorOp.intVecAddInt(vect, 10);
//        System.out.println(vect);
//
//        vec = VectorOp.doubleVecAddInt(vec, 10);
//        System.out.println(vec);
//
//        vect = VectorOp.intSubIntVec(10, vect);
//        System.out.println(vect);
//
//        vec = VectorOp.doubleVecSubDouble(vec, 10);
//        System.out.println(vec);
//
//        System.out.println(new IntVector(5, 10));
//
//        System.out.println(Matrix.of(
//                Arrays.asList("a", "b", "c"),
//                Arrays.asList("d", "e", "f")
//                )
//        );

        DoubleMatrix mat = DoubleMatrix.ofDoubles(Size.sizeOf(2, 2), 0, 1, 2, 3);

        System.out.println(mat);

        DoubleMatrix mat1 = DoubleMatrix.ofDoubles(Arrays.asList(Arrays.asList(0, 1), Arrays.asList(2, 3)));

        System.out.println(mat1);
    }
}