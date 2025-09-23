package ru.ssau.tk.swc.labs.functions;

public class LinkedListTabulatedFunction extends AbstractTabulatedFunction implements Insertable, Removable{
    private Node head = null;

    private void addNode(double x, double y){
        if (head==null){
            head = new Node();
            head.x=x;
            head.y=y;
            head.next=head;
            head.prev=head;
        }
        else{
            Node last=head.prev;
            Node node= new Node();
            last.next = node;
            head.prev = node;
            node.next=head;
            node.prev=last;
            node.x=x;
            node.y=y;

        }
        count++;
    }
    public LinkedListTabulatedFunction(double[] xValues, double[] yValues){
        if (xValues.length != yValues.length) {
            throw new IllegalArgumentException("Количество точек должно совпадать!");
        }

        if (xValues.length < 2) {
            throw new IllegalArgumentException("Нужно как минимум 2 точки!");
        }

        for (int i = 1; i < xValues.length; i++) {
            if (xValues[i] <= xValues[i - 1])
                throw new IllegalArgumentException("xValues значения должны находиться в порядке возрастания!");
        }

        for(int i=0; i<xValues.length; i++){
            addNode(xValues[i], yValues[i]);
        }
    }
    public LinkedListTabulatedFunction(MathFunction source, double xFrom, double xTo, int count){
        if (xFrom > xTo){
            double temp =xFrom;
            xFrom = xTo;
            xTo = temp;
        }

        double step = (xTo - xFrom) / (count - 1);

        for (int i = 0; i < count; i++) {
            double x = xFrom + i * step;
            double y = source.apply(x);
            addNode(x, y);
        }
    }
    @Override
    public int getCount(){
        return count;
    }
    @Override
    public double leftBound(){
        return head.x;
    }
    @Override
    public double rightBound(){
        return head.prev.x;
    }

    private Node getNode(int index){
        Node temp = head;
        for(int i=0; i<index; i++){
            temp=temp.next;
        }
        return temp;
    }
    @Override
    public double getX(int index){
        return getNode(index).x;
    }
    @Override
    public double getY(int index){
        return getNode(index).y;
    }
    @Override
    public void setY(int index, double value){
        getNode(index).y=value;
    }
    @Override
    public int indexOfX(double x){
        Node temp = head;
        int i =0;
        do{
            if(temp.x==x){
                return i;
            }
            i++;
            temp=temp.next;
        }while(temp!=head);
        return -1;
    }
    @Override
    public int indexOfY(double y){
        Node temp = head;
        int i =0;
        do{
            if(temp.y==y){
                return i;
            }
            i++;
            temp=temp.next;
        }while(temp!=head);
        return -1;
    }
    @Override
    public int floorIndexOfX(double x){ //макс х < заданного
        if (head.prev.x<x){
            return count;
        }
        if (head.x>x){
            return 0;
        }
        Node temp = head.prev; //идем сконнца тк х отсорт.
        int i =count-1;
        do{
            if(temp.x<=x){
                return i;
            }
            i--;
            temp=temp.prev;
        }while(temp!=head);
        return 0;
    }
    @Override
    public double extrapolateLeft(double x){ //x должен быть между 0 и 1 индексами
        if (head==head.next){
            return head.y;
        }
        return interpolate(x,head.x, head.next.x, head.y, head.next.y);
    }
    @Override
    public double extrapolateRight(double x){
        if (head==head.next){
            return head.y;
        }
        return interpolate(x,head.prev.prev.x, head.prev.x, head.prev.prev.y, head.prev.y);
    }
    @Override
    public double interpolate (double x, int floorIndex){
        if (head==head.next){
            return head.y;
        }
        return interpolate(x, getX(floorIndex), getX(floorIndex+1), getY(floorIndex), getY(floorIndex+1));
    }

    @Override
    public void insert (double x, double y){
        if (head == null)
            addNode(x, y);

        else {
            int i = 0;
            while (getX(i) < x && i < count)
                i++;

            if (i == count)
                addNode(x, y);

            else if (getX(i) == x)
                setY(i, y);

            else {
                Node element = new Node();
                element.x = x;
                element.y = y;
                Node temp = getNode(i);
                temp.prev.next = element;
                element.prev = temp.prev;
                element.next = temp;
                temp.prev = element;
                count++;
            }
        }
    }
    @Override
    public void remove(int index){
        Node temp=getNode(index);
        if(temp==temp.next){
            head=null;
            count=0;
            return;
        }
        if(temp==head){
            head=temp.next;
        }
        temp.next.prev=temp.prev;
        temp.prev.next=temp.next;
        count--;
    }
}
