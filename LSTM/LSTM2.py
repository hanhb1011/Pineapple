""" Recurrent Neural Network.
A Recurrent Neural Network (LSTM) implementation example using TensorFlow library.
This example is using the MNIST database of handwritten digits (http://yann.lecun.com/exdb/mnist/)
Links:
    [Long Short Term Memory](http://deeplearning.cs.cmu.edu/pdfs/Hochreiter97_lstm.pdf)
    [MNIST Dataset](http://yann.lecun.com/exdb/mnist/).
Author: Aymeric Damien
Project: https://github.com/aymericdamien/TensorFlow-Examples/
"""
from __future__ import print_function

import sys

from tensorflow.python.tools import freeze_graph
from tensorflow.python.tools import optimize_for_inference_lib

import tensorflow as tf
from tensorflow.contrib import rnn

import csv

MODEL_NAME = 'test2'


"""
이전노드위도, 이전노드경도, 현재위도, 현재경도, 다음노드위도, 다음노드경도
이탈
"""




# Training Parameters
learning_rate = 0.001
training_steps = 10000
batch_size = 128
display_step = 200

# Network Parameters
num_input = 6 # 입력변수의 크기
timesteps = 1
num_hidden = 128 # hidden layer num of features
num_classes = 2 # MNIST total classes (0-9 digits)


# tf Graph input
X = tf.placeholder(tf.float32, [None, timesteps, num_input], name='I')
Y = tf.placeholder(tf.float32, [None, num_classes])

# Define weights
weights = {
    'out': tf.Variable(tf.random_normal([num_hidden, num_classes], dtype=tf.float32), name='W')
}
biases = {
    'out': tf.Variable(tf.random_normal([num_classes], dtype=tf.float32), name='b')
}


def RNN(x, weights, biases):

    # Prepare data shape to match `rnn` function requirements
    # Current data input shape: (batch_size, timesteps, n_input)
    # Required shape: 'timesteps' tensors list of shape (batch_size, n_input)

    # Unstack to get a list of 'timesteps' tensors of shape (batch_size, n_input)
    x = tf.unstack(x, timesteps, 1)


    # Define a lstm cell with tensorflow
    lstm_cell = rnn.BasicLSTMCell(num_hidden, forget_bias=1.0)

    # Get lstm cell output
    outputs, states = rnn.static_rnn(lstm_cell, x, dtype=tf.float32)

    # Linear activation, using rnn inner loop last output
    return tf.matmul(outputs[-1], weights['out']) + biases['out']


logits = RNN(X, weights, biases)
prediction = tf.nn.softmax(logits, name='O')

# Define loss and optimizer
loss_op = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(
    logits=logits, labels=Y))
optimizer = tf.train.GradientDescentOptimizer(learning_rate=learning_rate)
train_op = optimizer.minimize(loss_op)

# Evaluate model (with test logits, for dropout to be disabled)
correct_pred = tf.equal(tf.argmax(prediction, 1), tf.argmax(Y, 1))
accuracy = tf.reduce_mean(tf.cast(correct_pred, tf.float32))

# Initialize the variables (i.e. assign their default value)
init = tf.global_variables_initializer()
saver = tf.train.Saver()
# Start training
with tf.Session() as sess:

    # Run the initializer
    sess.run(init)

    tf.train.write_graph(sess.graph_def, '.', 'test2.pbtxt')

    data = []
    with open('trainingData.csv', 'r') as csvfile:
        plot = csv.reader(csvfile, delimiter=',')
        for row in plot:
            data += [row]


    size = len(data) - len(data)%batch_size

    batch_x = []
    batch_y = []
    count = 0
    for i in range(0, size):
        if (i != 0) and (i % batch_size == 0):
            sess.run(train_op, feed_dict={X: batch_x, Y: batch_y})
            count = count + 1

            if (count % display_step == 0) or (count == 1):
                # Calculate batch loss and accuracy
                loss, acc = sess.run([loss_op, accuracy], feed_dict={X: batch_x,
                                                                     Y: batch_y})
                print("Step " + str(i//batch_size) + ", Minibatch Loss= " + \
                      "{:.4f}".format(loss) + ", Training Accuracy= " + \
                      "{:.3f}".format(acc))

            batch_x.clear()
            batch_y.clear()

        batch_x.append([[data[i][0], data[i][1], data[i][2], data[i][3], data[i][4], data[i][5]]])
        if data[i][0] == 0:
            batch_y.append([1, 0])
        else:
            batch_y.append([0, 1])

    saver.save(sess, './test2.ckpt')
    print("Optimization Finished!")


    # Calculate accuracy for test data
    data2 = []
    with open('testData.csv', 'r') as csvfile2:
        plot2 = csv.reader(csvfile2, delimiter=',')
        for row in plot2:
            data2 += [row]

    test_data = []
    test_label = []

    for i in range(0, 128):
        test_data.append([[data[i][0], data[i][1], data[i][2], data[i][3], data[i][4], data[i][5]]])
        if data[i][0] == 0:
            test_label.append([1, 0])
        else:
            test_label.append([0, 1])

    print("Testing Accuracy:", \
        sess.run(accuracy, feed_dict={X: test_data, Y: test_label}))

    print(sess.run(prediction, feed_dict={X: [[[129.21091504368758,34.26426359714899,129.2109475784393,34.264443489735775,129.21091291235834,34.264261465819736]]]}))

input_graph_path = MODEL_NAME+'.pbtxt'
checkpoint_path = './'+MODEL_NAME+'.ckpt'
input_saver_def_path = ""
input_binary = False
output_node_names = "O"
restore_op_name = "save/restore_all"
filename_tensor_name = "save/Const:0"
output_frozen_graph_name = 'frozen_'+MODEL_NAME+'.pb'
output_optimized_graph_name = 'optimized_'+MODEL_NAME+'.pb'
clear_devices = True


freeze_graph.freeze_graph(input_graph_path, input_saver_def_path,
                          input_binary, checkpoint_path, output_node_names,
                          restore_op_name, filename_tensor_name,
                          output_frozen_graph_name, clear_devices, "")



# Optimize for inference

input_graph_def = tf.GraphDef()
with tf.gfile.Open(output_frozen_graph_name, "rb") as f:
    data = f.read()
    input_graph_def.ParseFromString(data)


output_graph_def = optimize_for_inference_lib.optimize_for_inference(
        input_graph_def,
        ['I'], # an array of the input node(s)
        ['O'], # an array of output nodes
        tf.float32.as_datatype_enum)

# Save the optimized graph

f = tf.gfile.FastGFile(output_optimized_graph_name, "w")
f.write(output_graph_def.SerializeToString())


print(output_graph_def)

# tf.train.write_graph(output_graph_def, './', output_optimized_graph_name)





##########

# #test.pb로 protobuffer 저장
    # tf.train.write_graph(sess.graph_def,"./","test.pb", False)
    #
    # #체크
    # g = tf.GraphDef()
    # g.ParseFromString(open("test.pb", "rb").read())
    # print([n for n in g.node])