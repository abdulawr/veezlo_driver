package com.veezlo.veelzodriver.DataContainer;

import java.util.ArrayList;
import java.util.List;

public class GetFAQ {

    public  ArrayList<HelpContainer> list;

    public GetFAQ() {
        list=new ArrayList<>();
    }

    public void addData(String title,String des)
    {
       HelpContainer container=new HelpContainer();
       container.setTitle(title);
       container.setDes(des);
       list.add(container);
    }

    public void AssignData()
    {
        addData("What is Veezlo?","Veezlo is a mobile app through which you can earn money by wrapping your car in the advertisements. You will be paid for every mile you promote the brand on the roads. Its free money. Drive with Veezlo as you would drive in routine and convert your travels into money. You can do your routine things while still earning with Veezlo.");
        addData("What is a vehicle wrap?","It’s temporarily covering of the vehicles’ body using stickers or vinyl material. We print the ad digitally on the vinyl to give you the peace of mind as well as an earning opportunity. Vinyl wraps are commonly used by vehicle owners to protect the paint from scratches and fading. Wraps are also used for giving a modern, elegant and classy look to your car. We just do in a fun and free way for you. Instead, we pay you for just doing that.");
        addData("Who are Ambassadors?","Everyone who registers the vehicle and drives with us is called an Ambassador. Ambassadors are our champions who make a real difference for brands on the road. They represent a brand by being an Ambassador of that brand. We respect them and always appreciate them for doing the great work.");
        addData("Am I required to register with Veezlo to start earning?","Everyone who wants to become part of the Ambassadors great community and earn money with Veezlo must be registered on the Platform. Registration is necessary for us to understand about you and your vehicle so that we can match you with the most relevant campaigns. It is also necessary to ensure the safety of the community and keep the operations healthy.");
        addData("How much can an Ambassador earn?","It depends on what type of car you drive and which service type you have signed up for. Use the calculator to estimate your earnings.");
        addData("Is it free to register with Veezlo?","Absolutely free. From registration to training, from campaign to launch, everything is free for Ambassadors. If any of Veezlo’s officers ask you to pay for registration then report on contact@veezlo.com.");
        addData("How to register online?","Kindly register yourself at ambassador.veezlo.com.");
        addData("What are the requirements to become an Ambassador?",". Car registration book\n" +
                ". Government ID\n" +
                ". Driving license\n" +
                ". Q/A Form\n" +
                ". VCR Form\n" +
                ". Average 100kms/per day mileage\n" +
                ". 2000+ and newer models are preferred");
        addData("Is training mandatory?","Training is mandatory depending on your region. The training can be provided to you online or in-class. However, you will not be given access to your account without participating in the training. Kindly ensure your participation for enjoying Veezlo to its fullest.");
        addData("My account is still not activated?","It usually takes 24 hours to activate your account fully. Sometimes, additional documents are required and you will be informed. Also, there are instances, when documents verification is unsuccessful. Get in touch with a support officer to guide you further.");
        addData("What if I do not have a Government ID?","Government ID is your identity proof. It proves that you are eligible to drive on the road. In most states, the minimum age requirement is 18. So you need to provide the ID to ensure your age. It also helps us to differentiate you from other Ambassadors and facilitate payments disbursement.");
        addData("What if I don’t have a driving license?","You need to obtain the driving license to become part of the Veezlo. We encourage safe driving and promote a healthy environment. If you don’t have the license, we will ask you to apply for it. Once your license arrives, we start the registration process.");
        addData("What if I don’t have Car registration documents?","You are required to present the documents at the time of registration. It is mandatory for verification and differentiating your vehicle from other vehicles.");
        addData("What if I register myself with Veezlo but does not participate in training?","Your account will not be activated until you pass the training. If you have missed out the training, just inform us and we will give you another slot for training.");
        addData("How do I install stickers on my car?","Once you accept a campaign request, you will be given a time slot and location for installation where our team will install the stickers on your car.");
        addData("Do stickers damage the car’s paint?","We use high-quality material on the cars with the industry-wide used standard procedure of installation. Take your time to learn how to remove the stickers with industry standard procedure. Veezlo doesn’t take any responsibility for any damaged area of the car.");
        addData("Do I have an option to choose the ad campaign for my vehicle?","You have the option to either accept or reject a campaign request. However, beware that it has a direct implication on your earnings, acceptance rate and future campaign opportunities.");
        addData("What is acceptance, cancellation and completion rate?","The acceptance rate is the number of campaign requests you have accepted out of the total number of campaign requests. Higher is the better. The cancellation rate is the number of campaign requests you have rejected out of the total number of campaign requests. Lower is the better. Completion rate is the number of campaigns you have completed successfully out of the total number of accepted request. Higher is the better.");
        addData("Why is there acceptance, cancellation and completion rate?","It is your proof of reliability that how much Veezlo can depend on you. Our systems detect and trust the ambassadors with higher acceptance and completion rate. They are the people who will likely to complete the tasks if they are given any task. On the other hand, ambassadors with low acceptance and completion rate have low reliability. Veezlo systems are not sure whether they are reliable and will complete the tasks if they are given any. That’s why ambassadors with higher acceptance and completion rate are given more opportunities and offered campaigns first.");
        addData("How can I improve my acceptance and completion rate and avoid cancellation?","This is easy. If you want to least cancel the campaigns then turn on the vacation mode when you are not ready to accept the campaigns. When you are on vacations, our systems will not send you campaign requests.\n" +
                "Moreover, you can easily increase the acceptance rate by accepting the campaign requests every time our systems send you a request. You can cancel the ones you don’t like but it will lower your acceptance rate. However, the completion rate is the most critical indicator for your success at Veezlo. Always choose campaigns wisely which you know you can complete. The wiser you choose, the less is the chance you will withdraw from the campaigns. Remember, by rule, you can’t withdraw from campaigns. Read terms and conditions for more information about this.");
        addData("What are the ratings?","Advertiser rate you at the end of the campaign from the scale of one to five based on the service you have provided. They also provide feedback on how the campaign went.\n" +
                "If your ratings are higher, then many advertisers will want to work with you. If your ratings are low, advertisers would assume that you do not provide excellent services.");
        addData("How can I improve my ratings?","The easiest way to improve your ratings is to provide exceptional services to advertisers, take care of the material, avoid fraud, provide photos of your vehicle in time and deliver the requirements.");
        addData("I have registered with Veezlo, but I have not received any campaign up till now?","There are several reasons why you have not received the campaigns.\n" +
                "1. There are not enough campaigns in your area.\n" +
                "2. The advertisers are targeting and running campaigns in zones other than yours.\n" +
                "3. You have turned on vacation mode and forgot it.\n" +
                "4. You are in a city other than the city you are signed up for.\n" +
                "5. You do not check notifications and campaign requests.\n" +
                "6. You are too slow to check out the requests & other fellows accept the opportunities quicker than you.\n" +
                "7. Your account is suspended or blocked.\n" +
                "8. The campaign for your car type is not available as yet.\n" +
                "9. You have signed up for fewer services.\n" +
                "10. Advertisers create campaigns in different service type than yours.");
        addData("I have received campaigns in the beginning, but now I hardly see any campaign on the App?","There are several reasons why you can experience this kind of situation.\n" +
                "1. You have canceled/rejected too many campaigns.\n" +
                "2. Your cancellation rate is high due to which system gives campaigns to those with low cancellation rate. Your reliability is a question mark.\n" +
                "3. Veezlo systems trust Ambassadors with higher acceptance and completion rate and you fall below the minimum required rate.\n" +
                "4. Your account is suspended or blocked.\n" +
                "5. You were previously involved in the fraud due to which system offers campaign to more trustworthy ambassadors.\n" +
                "6. Your ratings are not good. Previous advertisers rated you low due to which other advertisers do not want to work with you.\n" +
                "7. You withdrew from campaigns in the middle of the running campaigns.\n" +
                "8. You didn’t provide the photos on-demand.\n" +
                "9. You violated laws and policies.\n" +
                "10. You were found in bad driving practices.");
        addData("How Veezlo will pay me?","Through your preferred payment method. This can be through a bank transfer, mobile account, jazz cash or vouchers.");
        addData("When will Veezlo pay me for my completed campaigns?","The standard bank clearing time is 7 business days. You can expect payments in 7 to 10 days.");
        addData("Why Veezlo reversed my earnings?","In case, you were involved in the fraud then your earnings will be reversed either partially or fully depending on the severity of the fraud. Also, if you withdraw from a running campaign then you will be subjected to a penalty to cover the print & installation cost that advertiser will have to incur again because of you.");
        addData("Why did I lose access to my account permanently?","Read the terms and conditions to know more about it. Generally, you lose access to your account when you are involved in the fraud, violated laws or policies.");
        addData("Can I submit an appeal to unblock my account?","You can submit your appeal but blocked accounts are rarely unblocked. We have a zero tolerance policy on fraud. Your appeal will be evaluated by the team to give a final decision. Every Ambassador is allowed to appeal only once.");
        addData("Can I take participate in two campaigns at once?","You can take part in one campaign at a time.");
        addData("Is it necessary to complete the tasks?","They are not obligatory but they are fun along with supplemental income. They include quests, flash mobs, taking photos of your car in popular public places or events, participating in a rally or just parking somewhere.");
        addData("Can I participate in another campaign after completing one campaign?","You can participate in other campaigns as well. When you complete a campaign successfully, our systems start to provide you more campaigns automatically.");
        addData("How can I get support on issues?","Visit the local office or call the support center for help.");
    }

    public  List<HelpContainer> getQuestion()
    {
        return list;
    }

}
