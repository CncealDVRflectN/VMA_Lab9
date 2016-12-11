package by.vma.lab9;

public class Main {
    private static class Matrix {
        public double[][] matrix;
        private int lines;
        private int columns;

        public Matrix(int lines, int columns) throws Exception {
            if (lines < 1 || columns < 1) {
                throw new Exception("Неверный размер.");
            }
            this.lines = lines;
            this.columns = columns;
            this.matrix = new double[lines][columns];
        }

        public Matrix(Matrix init) throws Exception {
            this(init.getLines(), init.getColumns());
            for (int i = 0; i < lines; i++) {
                for (int j = 0; j < columns; j++) {
                    this.matrix[i][j] = init.matrix[i][j];
                }
            }
        }

        public int getLines() {
            return lines;
        }

        public int getColumns() {
            return columns;
        }

        public void print() {
            for (double[] i : matrix) {
                for (double j : i) {
                    System.out.printf("%.5f", j);
                    System.out.print("  ");
                }
                System.out.println();
            }
        }

        public void swap(int fi, int fj, int si, int sj) {
            double tmp = matrix[fi][fj];
            matrix[fi][fj] = matrix[si][sj];
            matrix[si][sj] = tmp;
        }

        public void fillDefault() {
            double[][] a = {{0.6444, 0.0000, -0.1683, 0.1184, 0.1973},
                    {-0.0395, 0.4208, 0.0000, -0.0802, 0.0263},
                    {0.0132, -0.1184, 0.7627, 0.0145, 0.0460},
                    {0.0395, 0.0000, -0.0960, 0.7627, 0.0000},
                    {0.0263, -0.0395, 0.1907, -0.0158, 0.5523}};
            this.lines = 5;
            this.columns = 5;
            this.matrix = a;
        }

        public Vector mul(Vector vector) throws Exception {
            if (columns != vector.getLength()) {
                throw new Exception("Неверная матрица или вектор.");
            }
            Vector result = new Vector(vector.getLength());
            for (int i = 0; i < lines; i++) {
                result.vector[i] = 0;
                for (int j = 0; j < columns; j++) {
                    result.vector[i] += matrix[i][j] * vector.vector[j];
                }
            }
            return result;
        }

        public Matrix mul(Matrix mtr) throws Exception {
            if (columns != mtr.getLines()) {
                throw new Exception("Неверная матрица.");
            }
            Matrix result = new Matrix(lines, mtr.getColumns());
            for (int i = 0; i < result.getLines(); i++) {
                for (int j = 0; j < result.getColumns(); j++) {
                    result.matrix[i][j] = 0;
                    for (int k = 0; k < columns; k++) {
                        result.matrix[i][j] += this.matrix[i][k] * mtr.matrix[k][j];
                    }
                }
            }
            return result;
        }

        public Matrix transpose() throws Exception {
            if (lines != columns) {
                throw new Exception("Неверная матрица.");
            }
            Matrix result = new Matrix(this);
            for (int i = 0; i < lines; i++) {
                for (int j = i + 1; j < columns; j++) {
                    result.swap(i, j, j, i);
                }
            }
            return result;
        }
    }

    private static class Vector {
        public double[] vector;
        private int length;

        public Vector(int length) throws Exception {
            if (length < 1) {
                throw new Exception("Неверный размер.");
            }
            this.length = length;
            vector = new double[length];
        }

        public Vector(Vector init) throws Exception {
            this(init.getLength());
            for (int i = 0; i < length; i++) {
                this.vector[i] = init.vector[i];
            }
        }

        public int getLength() {
            return length;
        }

        public void fillDefault() {
            double[] vect = {1, 0, 0, 0, 0};
            this.length = 5;
            this.vector = vect;
        }

        public void print(boolean exponent) {
            for (double item : vector) {
                if (exponent) {
                    System.out.printf("%e\n", item);
                } else {
                    System.out.printf("%.5f\n", item);
                }
            }
        }

        public Vector mul(double num) throws Exception {
            Vector result = new Vector(length);
            for (int i = 0; i < length; i++) {
                result.vector[i] = this.vector[i] * num;
            }
            return result;
        }

        public Vector subtract(Vector sub) throws Exception {
            if (length != sub.getLength()) {
                throw new Exception("Неверный вектор.");
            }
            Vector result = new Vector(length);
            for (int i = 0; i < length; i++) {
                result.vector[i] = this.vector[i] - sub.vector[i];
            }
            return result;
        }

        public double normI() {
            double max = Math.abs(vector[0]);
            for (int i = 1; i < length; i++) {
                if (Math.abs(vector[i]) > max) {
                    max = Math.abs(vector[i]);
                }
            }
            return max;
        }
    }

    private static Matrix A;
    private static Vector eigen;
    private static final int n = 5;
    private static final double epsilon = 0.00001;

    public static void main(String[] args) {
        Vector r;
        double lambda;
        try {
            A = new Matrix(n, n);
            A.fillDefault();
            A = A.transpose().mul(A);
            System.out.println("Матрица A:");
            A.print();
            System.out.println();
            lambda = methodPower();
            System.out.println("Максимальное собственное значение: " + lambda);
            System.out.println("Собственный вектор соответствующий максимальному собственному значению: ");
            eigen.print(false);
            System.out.println();
            r = A.mul(eigen).subtract(eigen.mul(lambda));
            System.out.println("Вектор невязки собственного вектора: ");
            r.print(true);
            System.out.println();
            System.out.println("Норма вектора невязки собственного вектора: " + r.normI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double methodPower() throws Exception {
        Vector ycur = new Vector(n);
        Vector ynext;
        Vector z = new Vector(n);
        double lambdanext = 0;
        double norm;
        boolean flag = true;
        int counter = 0;
        ycur.fillDefault();
        while (flag) {
            ynext = A.mul(ycur);
            flag = false;
            norm = ynext.normI();
            for(int i = 0; i < n; i++) {
                lambdanext = ynext.vector[i] / ycur.vector[i];
                if(!flag && Math.abs(lambdanext - z.vector[i]) > epsilon){
                    flag = true;
                }
                z.vector[i] = lambdanext;
                ycur.vector[i] = ynext.vector[i] / norm;
            }
            counter++;
        }
        eigen = new Vector(ycur);
        System.out.println("Количество итераций: " + counter);
        System.out.println();
        return lambdanext;
    }
}
