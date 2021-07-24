#include <wiringPi.h>
#include <softPwm.h>
#include <pthread.h>
#include <unistd.h>
#include <iostream>
#include <sys/socket.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <string.h>

#define	LED	17

int sockfd, newsockfd;
int portno = 40300;
int buffer[1024];
struct sockaddr_in6 serv_addr, cli_addr;
socklen_t size;
int n;

int x = 0;
int* start_pos;


void servo_close()
{
	pinMode(LED, INPUT);
	pullUpDnControl(LED, PUD_DOWN);
}


void move_backwards(int value)
{
	start_pos = &x;

	for (int i = *start_pos; i > value; i--)
	{
		softPwmWrite(LED, i);
		delay(20);
	}

	*start_pos = value;
}


void move_forward(int value)
{
	start_pos = &x;

	softPwmCreate(LED, *start_pos, 180);
	pullUpDnControl(LED, PUD_OFF);
	pinMode(LED, SOFT_PWM_OUTPUT);

	if (value < *start_pos)
		move_backwards(value);

	for (int i = *start_pos; i < value; i++)
	{
		softPwmWrite(LED, i);
		delay(10);
	}

	*start_pos = value;
}

void move_to_start_end(int value)
{
	start_pos = &x;

	softPwmCreate(LED, *start_pos, 180);
	pullUpDnControl(LED, PUD_OFF);
	pinMode(LED, SOFT_PWM_OUTPUT);

	switch (value)
	{
	case 100:
		for (int i = *start_pos; i > 0; i--)
		{
			softPwmWrite(LED, i);
			delay(10);
			*start_pos = i;
		}
		break;
	case 200:
		for (int i = *start_pos; i < 25; i++)
		{
			softPwmWrite(LED, i);
			delay(10);
			*start_pos = i;
		}
	default:
		break;
	}
	
}


int main(void)
{
	wiringPiSetupSys();

	pinMode(LED, OUTPUT);

	int my_position;


	sockfd = socket(AF_INET6, SOCK_STREAM, 0);
	if (sockfd < 0)
	{
		std::cout << "Error..." << std::endl;
		exit(1);
	}

	std::cout << "Connection created." << std::endl;

	bzero((sockaddr*)&serv_addr, sizeof(serv_addr));

	serv_addr.sin6_family = AF_INET6;
	serv_addr.sin6_addr = in6addr_any;
	serv_addr.sin6_port = htons(portno);


	int one = 1;
	setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &one, sizeof(one));

	if (bind(sockfd, (sockaddr*)&serv_addr, sizeof(serv_addr)) < 0)
	{
		std::cout << "Error binding socket..." << std::endl;
		exit(1);
	}

	size = sizeof(cli_addr);
	std::cout << "Looking for clients..." << std::endl;

	listen(sockfd, 1);


	do {
		newsockfd = accept(sockfd, (struct sockaddr*)&cli_addr, &size);
		if (newsockfd < 0)
		{
			std::cout << "Error on accepting" << std::endl;
			exit(1);
		}
		else do
		{
			if ((n = read(newsockfd, buffer, sizeof(buffer))) < 0)
			{
				std::cout << "Error..." << std::endl;
				exit(1);
			}
			else
			{
				std::cout << "Move to position " << buffer[0] << std::endl;
				//if (buffer[0] == 1)
				my_position = buffer[0];
				if (my_position == 0 || my_position == 26)
				{
					move_to_start_end(my_position);
				}
				move_forward(my_position);
			}
		} 
		while (n > 0);
	} 	
	while (true);


	close(newsockfd);
	close(sockfd);

	servo_close();
	
	return 0;
}